import * as RT from "@/views/resetterrf/MachineEdit/RoutineTypes";

/*interface TestData {
  tableItems: RT.UserRoutine;
}*/

//let testTableData

/*export default () => {
  let testRoutines = new Array<RT.ExistingUserRoutine>();
  testRoutines = [
    {
      id: 44,
      name: "Keep offline during afternoon",
      type: RT.UserRoutineType.KeepOfflinePeriod,
      timeCreated: new Date("June 14, 2019 10:46:22"),
      enabled: false
    },
    {
      id: 8,
      name: "Reset every 6 hours",
      type: RT.UserRoutineType.ResetOnInterval,
      timeCreated: new Date("June 14, 2019 23:55:07"),
      enabled: true
    },
    {
      id: 93,
      name: "Power on in 45 minutes",
      type: RT.UserRoutineType.OneTimeAction,
      timeCreated: new Date("June 15, 2019 13:04:41"),
      enabled: true
    }
  ];

  return testRoutines;
};*/

// TODO: Convert to appropriate types
const testRoutines: RT.ExistingRoutine[] = [
  {
    type: "Existing",
    userRoutine: {
      id: 44,
      name: "Keep offline during afternoon",
      timeCreated: new Date("June 14, 2019 10:46:22"),
      enabled: false
    },
    button: ""
  },
  {
    type: "Existing",
    userRoutine: {
      id: 8,
      name: "Reset every 6 hours",
      timeCreated: new Date("June 14, 2019 23:55:07"),
      enabled: true
    },
    button: ""
  },
  {
    type: "Existing",
    userRoutine: {
      id: 93,
      name: "Power on in 45 minutes",
      timeCreated: new Date("June 15, 2019 13:04:41"),
      enabled: true
    },
    button: ""
  }
];

export default testRoutines;
