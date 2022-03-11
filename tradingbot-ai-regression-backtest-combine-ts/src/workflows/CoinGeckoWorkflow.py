from datetime import datetime, timedelta
import statistics
from typing import Any, List, Union
from webbrowser import get
import pandas as PD
from util.FileWrappers import COINGECKO_INPUT_LOCAL_PATH, FileWrapper, FolderWrapper
from Models import InputArgs, SegmentInfo
from util.S3Wrappers import AWS_REGION, S3_COINGECKO_INPUT_BUCKET_NAME, S3Wrapper_Down

ROW_DURATION: timedelta = timedelta(hours=12)


class CoinGeckoWorkflow():
    args: InputArgs
    segmentInfo: SegmentInfo

    # download files
    # load files
    # transform into output frame

    def __init__(self, args: InputArgs, segmentInfo: SegmentInfo):
        self.args = args
        self.segmentInfo = segmentInfo
        return

    def run(self) -> PD.DataFrame:
        print(f"CoinGeckoWorkflow | Downloading S3 bucket...")
        self.downloadBucket()

        print(f"CoinGeckoWorkflow | Generating standardized input frame...")
        result: PD.DataFrame = self.generateStandardizedFrame()

        print(f"CoinGeckoWorkflow | Done.")
        return result

    def downloadBucket(self) -> None:
        downloadFolderFilter: str = ""
        downloadFileTypeFilters: List[str] = ["coingecko_historical_"]

        s3Down: S3Wrapper_Down = S3Wrapper_Down(
            awsRegion=AWS_REGION, bucketName=S3_COINGECKO_INPUT_BUCKET_NAME,
            localFolderPath=COINGECKO_INPUT_LOCAL_PATH,
            fileTypeFilters=downloadFileTypeFilters,
            fileFilters=[downloadFolderFilter])

        s3Down.downloadObjects_filtered()
        return

    def getCsvInputFrame(self) -> PD.DataFrame:
        inputFolder: FolderWrapper = FolderWrapper(COINGECKO_INPUT_LOCAL_PATH)
        inputFile: FileWrapper = inputFolder.getFile(
            f"coingecko_historical_{self.segmentInfo.pair}.csv")
        return inputFile.loadFile_csvFrame()

    def convertInputFrameTo12h(
            self, coinGeckoFrame_raw: PD.DataFrame) -> PD.DataFrame:
        startRowInd: int = coinGeckoFrame_raw.index.get_loc(
            self.segmentInfo.firstSegmentStartTime, method='ffill')
        endRowInd: int = coinGeckoFrame_raw.index.get_loc(
            self.segmentInfo.lastSegmentEndTime, method='ffill')

        colNames: List[str] = ["price"]
        coinGeckoFrame_12h: PD.DataFrame = PD.DataFrame(
            data=[], columns=colNames)

        for r in range(startRowInd, endRowInd + 1):
            rawRowPrice_now: float = coinGeckoFrame_raw["price"][r]
            rawRowTime_now: datetime = coinGeckoFrame_raw.index[r].to_pydatetime(
            )

            rawRowPrice_next: float = coinGeckoFrame_raw["price"][r + 1]
            rawRowTime_next: datetime = rawRowTime_now + ROW_DURATION
            middlePriceAvg: float = statistics.mean(
                [rawRowPrice_now, rawRowPrice_next])

            coinGeckoFrame_12h.loc[rawRowTime_now] = rawRowPrice_now
            coinGeckoFrame_12h.loc[rawRowTime_next] = middlePriceAvg

        return coinGeckoFrame_12h

    def generatePriceChangeFrame(
            self, coinGeckoFrame_12h: PD.DataFrame) -> PD.DataFrame:
        colNames: List[str] = ["price", "priceChange12h",
                               "priceChange24h", "priceChangeSegmentPeriod"]
        coinGeckoFrame_change: PD.DataFrame = PD.DataFrame(
            data=[], columns=colNames)

        def getPriceChangePct(rowInd: int, timePeriod: timedelta) -> Union[float, None]:
            rowTime: datetime = coinGeckoFrame_12h.index[rowInd]
            rowPrice: float = coinGeckoFrame_12h["price"][rowInd]

            prevTime: datetime = rowTime - timePeriod
            if prevTime < coinGeckoFrame_12h.index.min():
                return None

            prevRowInd: int = coinGeckoFrame_12h.index.get_loc(
                prevTime, method='ffill')

            prevRowPrice: float = coinGeckoFrame_12h["price"][prevRowInd]
            priceChgPct: float = (rowPrice - prevRowPrice) / prevRowPrice
            return priceChgPct

        for rowInd in range(0, len(coinGeckoFrame_12h)):
            rowTime: datetime = coinGeckoFrame_12h.index[rowInd]
            price: float = coinGeckoFrame_12h["price"][rowInd]

            priceChange_12h: Union[float, None] = getPriceChangePct(
                rowInd, ROW_DURATION)
            priceChange_24h: Union[float, None] = getPriceChangePct(
                rowInd, ROW_DURATION * 2)
            priceChange_sp: Union[float, None] = getPriceChangePct(
                rowInd, timedelta(seconds=self.segmentInfo.segmentDurationS))

            newRowData: List[float] = [
                price, priceChange_12h, priceChange_24h, priceChange_sp]
            coinGeckoFrame_change.loc[rowTime] = newRowData

        return coinGeckoFrame_change

    def generateSegmentIdFrame(
            self, coinGeckoFrame_change: PD.DataFrame) -> PD.DataFrame:
        colNames: List[str] = ["segmentId", "price", "priceChange12h",
                               "priceChange24h", "priceChangeSegmentPeriod"]

        coinGeckoFrame: PD.DataFrame = PD.DataFrame(
            data=[], columns=colNames)

        geckoFrameTotalDurationSec: int = len(
            coinGeckoFrame_change) * ROW_DURATION.total_seconds()
        idealSegmentCount: float = geckoFrameTotalDurationSec / self.segmentInfo.segmentDurationS

        segmentDurationFillRatio: float = idealSegmentCount / \
            len(self.segmentInfo.segmentIds)

        currSegmentIdInd: int = 0
        currSegmentTimeLeft: timedelta = timedelta(
            seconds=self.segmentInfo.segmentDurationS) * segmentDurationFillRatio

        for rowInd in range(0, len(coinGeckoFrame_change)):
            rowTime: datetime = coinGeckoFrame_change.index[rowInd]
            row: Any = coinGeckoFrame_change.loc[rowTime]

            rowSegmentId: str = str(
                self.segmentInfo.segmentIds[currSegmentIdInd])
            newRow: Any = [
                rowSegmentId, row.price, row.priceChange12h, row.priceChange24h,
                row.priceChangeSegmentPeriod]
            coinGeckoFrame.loc[rowTime] = newRow

            currSegmentTimeLeft -= ROW_DURATION

            if currSegmentTimeLeft <= timedelta(seconds=0):  # ROW_DURATION
                if currSegmentIdInd < len(self.segmentInfo.segmentIds) - 1:
                    currSegmentIdInd += 1

                lastSegmentTimeLeft: timedelta = currSegmentTimeLeft

                currSegmentTimeLeft = timedelta(
                    seconds=self.segmentInfo.segmentDurationS) * segmentDurationFillRatio
                currSegmentTimeLeft += lastSegmentTimeLeft

        return coinGeckoFrame

    def generateStandardizedFrame(self) -> PD.DataFrame:
        coinGeckoFrame_raw: PD.DataFrame = self.getCsvInputFrame()
        coinGeckoFrame_raw = coinGeckoFrame_raw.set_index("snapped_at")
        coinGeckoFrame_raw.index = PD.to_datetime(coinGeckoFrame_raw.index)

        coinGeckoFrame_12h: PD.DataFrame = self.convertInputFrameTo12h(
            coinGeckoFrame_raw)

        coinGeckoFrame_change: PD.DataFrame = self.generatePriceChangeFrame(
            coinGeckoFrame_12h)

        coinGeckoFrame: PD.DataFrame = self.generateSegmentIdFrame(
            coinGeckoFrame_change)

        return coinGeckoFrame
