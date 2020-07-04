package testv2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class RecoverCustomers {

	public static void main(String[] args) throws Exception
	{
		//Scanner s = new Scanner(System.in);
		String in = "('Aoki','ca.hammereditor.net',1424937600000,NULL),('atails','ca.hammereditor.net',1423634400000,NULL),('BillCipher1','ca2.hammereditor.net',1471020691000,'billcipher@charter.net'),('BLC','hammerhost.hammereditor.net',1423486800000,NULL),('BLC2','hammerhost.hammereditor.net',1423486800000,NULL),('Brickitect','ca.hammereditor.net',1442856598000,NULL),('Couatl','ca.hammereditor.net',1424937600000,NULL),('DataBear','ca.hammereditor.net',1423468800000,NULL),('Dglider','ca2.hammereditor.net',1464207908000,'dgliderglide@gmail.com'),('EV0','ca.hammereditor.net',1433095147000,NULL),('Faraday','ca.hammereditor.net',1441840123000,NULL),('Hammereditor','ca.hammereditor.net',9999999999999,NULL),('LeJoeh','ca.hammereditor.net',1441995484000,NULL),('Nizza','ca.hammereditor.net',1443137408000,NULL),('NuclearNacho','ca.hammereditor.net',1436899134000,NULL),('OperatedPrune32','ca.hammereditor.net',1426618940000,NULL),('Panquake','ca2.hammereditor.net',1465434263000,'joey.sofia1@gmail.com'),('Panquake2','ca.hammereditor.net',1438536360000,NULL),('PiexTharo','ca.hammereditor.net',1434656317000,NULL),('PiexTharo2','ca.hammereditor.net',1434656317000,NULL),('rggbnnnnn','ca2.hammereditor.net',1469836293000,NULL),('rggbnnnnn2','ca.hammereditor.net',9999999999999,NULL),('shawn6183','ca2.hammereditor.net',1469750174000,NULL),('SlenderC','ca2.hammereditor.net',1461948060000,'drivintin@gmail.com'),('Summet','ca.hammereditor.net',1435624124000,NULL),('SuperFlaminninja','ca2.hammereditor.net',1461535014000,'jondavidc@yahoo.com'),('Tannermcmanus','ca.hammereditor.net',1439249733000,NULL),('TGChampion','ca.hammereditor.net',1457823783000,NULL),('VilsonPL','ca2.hammereditor.net',1462137479000,'milomilo2003@wp.pl'),('VilsonPL1','ca2.hammereditor.net',1467137955000,'milomilo2003@wp.pl'),('william341','ca.hammereditor.net',1436731448000,'bhegde8@gmail.com')"; //s.nextLine();
		
		in = in.replaceAll("\\),\\(", "\n");
		in = in.replaceAll(",", " ");
		in = in.replaceAll("\\)", " ");
		in = in.replaceAll("\\(", " ");
		in = in.replaceAll("'", " ");
		
		//System.out.println(in);
		//String[] lines = in.split("\n");
		Scanner d = new Scanner(in);
		List<Customer> l = new ArrayList<Customer> ();
		
		while (d.hasNextLine())
		{
			String cs = d.nextLine();
			cs = cs.trim();
			cs = cs.replaceAll("    ", " ");
			cs = cs.replaceAll("   ", " ");
			cs = cs.replaceAll("  ", " ");
			System.out.println(cs);
			
			String[] csl = cs.split(" ");
			String username = csl[0];
			String node = csl[1];
			long expire = Long.parseLong(csl[2]);
			String ma = csl[3];
			
			if (node.contains("ca2") && expire > System.currentTimeMillis())
			{
				Customer c = new Customer(username, expire, ma);
				l.add(c);
			}
			
			/*Scanner s = new Scanner(cs);
			s.useDelimiter(" ");
			System.out.println(cs);
			
			if (cs.isEmpty())
				continue;
			
			while (s.hasNext())
			{
				//System.out.println(s.next());
				String username = s.next();
				s.next();s.next();s.next();
				String node = s.next();
				s.next();
				long expire = Long.parseLong(s.next());
				String ma = s.next();
				
				if (expire > System.currentTimeMillis() && node.equals("ca2.hammereditor.net"))
				{
					Customer c = new Customer(username, expire, ma);
					l.add(c);
				}
				
				if (!n.isEmpty())// && !n.equals("NULL"))
				{
					System.out.println(n);
					switch (c)
					{
						case 1:
							
							break;
					}
					
					c++;
				}
			}
			
			s.close();*/
		}
		
		d.close();
		Collections.sort(l);
		System.out.println("\n\n\n");
		
		for (Customer c : l)
		{
			System.out.println(c);
			long timeLeftMs = c.expiryDateMs - System.currentTimeMillis();
			System.out.println((((timeLeftMs * 1.0) / (1.0 * (24 * 60 * 60 * 1000))) + 1) + " days remaining");
		}
	}

}
