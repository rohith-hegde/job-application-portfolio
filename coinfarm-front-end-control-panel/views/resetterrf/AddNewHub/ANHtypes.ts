export type OutputStep1_successful = {
  type: "successful";
  hubGlobalIP: string;
};

export type OutputStep1_failed = {
  type: "failed";
  errorMsg: string;
};

export type OutputStep1_init = {
  type: "init";
};

export type OutputStep1 = OutputStep1_failed | OutputStep1_successful | OutputStep1_init;

export type OutputStep2_successful = {
  type: "successful";
  hubGlobalIP: string;
  hubSerialNumber: string;
};

export type OutputStep2_failed = {
  type: "failed";
  errorMsg: string;
};

export type OutputStep2_init = {
  type: "init";
};

export type OutputStep2 = OutputStep2_failed | OutputStep2_successful | OutputStep2_init;

export type OutputStep3_successful = {
  type: "successful";
};

export type OutputStep3_failed = {
  type: "failed";
  errorMsg: string;
};

export type OutputStep3_init = {
  type: "init";
};

export type OutputStep3 = OutputStep3_failed | OutputStep3_successful | OutputStep3_init;
