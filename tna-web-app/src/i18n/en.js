import englishMessages from "jazasoft/lib/i18n/default-messages/js-language-english";

// const englishMessages = i18n["js-language-english"];

export default {
  ...englishMessages,
  app_name_short: "TNA",
  app_name_full: "Time and Action Calendar",
  dashboard: "Dashboard",
  forbidden: {
    title: "Forbidden",
    message: "Access Denied. You do not have enough privilege for this operation",
  },
  resources: {
    orders: {
      name: "Bundle & Ticket |||| Bundle & Ticket",
      fields: {
        internalPo: "Internal PO",
        garmentType: "Garment Type",
        seasonId: "Season",
        buyer: "Buyer",
        operationBulletinId: "Style OB",
        orderQty: "Order Qty",
        desc: "Description",
        style: "Style",
        colorInfoList: "Color Info List",
        colorInfo: {
          color: "Color",
          lots: "Lots",
          bundleSize: "Bundle Size",
          bundleTolerance: "Bundle Tolerance"
        }
      }
    },
    users: {
      name: "User |||| Users",
      fields: {
        fullName: "Full Name",
        username: "Username",
        email: "Email",
        mobile: "Mobile",
        roles: "Roles",
        roleList: "Roles",
        employeeId: "Employee Id",
        designationId: "Designation"
      }
    },
    operatorPerformance: {
      name: "Operator Performance |||| Operator Performance"
    },
    // operation bulletin
    operationBulletin: {
      name: "Operation Bulletin |||| Operation Bulletin"
    },
    operations: {
      name: "Operation Master |||| Operation Master",
      fields: {
        name: "Name",
        desc: "Description"
      }
    },
    tOperationBulletins: {
      name: "OB Template |||| OB Templates",
      fields: {
        name: "Template Name",
        styleDesc: "Style Description",
        createdAt: "Created On",
        createdBy: "Created By",
        targetEfficiency: "Target Efficiency",
        prodTarget: "Production Target",
        shiftMin: "Shift Minutes",
        remarks: "Remarks"
      }
    },
    operationBulletins: {
      name: "Style OB |||| Style OB",
      fields: {
        style: "Style",
        styleDesc: "Style Description",
        orderQty: "Order Qty",
        createdAt: "Created On",
        createdBy: "Created By",
        targetEfficiency: "Target Efficiency",
        prodTarget: "Production Target",
        shiftMin: "Shift Minutes",
        remarks: "Remarks"
      }
    },
    // library tabs
    buyers: {
      name: "Buyer |||| Buyers",
      fields: {
        name: "Buyer",
        code: "Code"
      }
    },
    parts: {
      name: "Bundle Part |||| Bundle Parts",
      fields: {
        name: "Name"
      }
    },
    departments: {
      name: "Department |||| Departments",
      fields: {
        name: "Department",
        desc: "Description"
      }
    },
    seasons: {
      name: "Season |||| Seasons",
      fields: {
        name: "Season"
      }
    },
    garmentTypes: {
      name: "Garment Type |||| Garment Types",
      fields: {
        name: "Name"
      }
    },
    fabricTypes: {
      name: "Fabric Type |||| Fabric Types",
      fields: {
        name: "Name"
      }
    },
    lines: {
      name: "Line |||| Lines",
      fields: {
        name: "Name",
        desc: "Description"
      }
    },
    sections: {
      name: "Section |||| Section",
      fields: {
        name: "Name",
        desc: "Description"
      }
    },
    machineTypes: {
      name: "Machine Type |||| Machine Types",
      fields: {
        name: "Name",
        desc: "Description"
      }
    },
    machines: {
      name: "Machine |||| Machines",
      fields: {
        name: "Name",
        desc: "Description"
      }
    },
    designations: {
      name: "Designation |||| Designations",
      fields: {
        name: "Name",
        desc: "Description",
        lineList: "Lines",
        lines: "Lines",
        level: "Level"
      }
    }
  }
};
