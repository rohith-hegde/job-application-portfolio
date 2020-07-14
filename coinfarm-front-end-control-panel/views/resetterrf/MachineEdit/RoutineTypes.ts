export type BaseRoutine = {
  //will be used for both pre-existing and newly created user routines
  id: number;
  name: string;
  timeCreated: Date;
  enabled: boolean;
};

export type ExistingRoutine = {
  type: "Existing";
  userRoutine: BaseRoutine;
  button: string; //this is a blank string needed for CoreUI table compatibility
};

export type NewRoutine = {
  type: "New";
  userRoutine: BaseRoutine;
  timing: UserRoutineTiming;
};

export enum TimingRepeatType {
  none,
  daily,
  weekly,
  monthly,
  annually
}

export type Period = {
  type: "Period";
  keep: "Online" | "Offline";
  repeat: TimingRepeatType;
  periodStartS: number; //the number of seconds from the startTime of the repeat type
  //that the period starts
  periodEndS: number; //the number of seconds from the startTime of the repeat type
  //that the period ends
  startTime: Date; //the absolute timestamp that the other timings are based on
};

export type ResetOnInterval = {
  type: "ResetOnInterval";
  intervalS: number; //the length of the interval, in seconds
  startTime: Date; //the absolute timestamp that the other timings are based on
};

export type OneTimeAction = {
  type: "OneTimeAction";
  startTime: Date; //the absolute timestamp that the other timings are based on
};

export type UserRoutineTiming = OneTimeAction | ResetOnInterval | Period;

export type UserRoutine = ExistingRoutine | NewRoutine;


