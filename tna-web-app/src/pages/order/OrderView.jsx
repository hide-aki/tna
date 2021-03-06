import React, { Component } from "react";
import { connect } from "react-redux";
import moment from "moment";

import {
  TextField,
  NumberField,
  SimpleShowLayout,
  crudGetOne,
  PageFooter,
  Button,
  BackButton,
  FunctionField,
  SAVING_START,
  RestMethods,
  SAVING_END,
  FETCH_START,
  FETCH_END,
  showNotification,
  PageHeader
} from "jazasoft";

import { dataProvider } from "../../App";

//Material-UI
import Divider from "@material-ui/core/Divider";
import Card from "@material-ui/core/Card";
import withStyles from "@material-ui/styles/withStyles";

// Icons
import EditIcon from "@material-ui/icons/Edit";
import HistoryIcon from "@material-ui/icons/History";
import PrintIcon from "@material-ui/icons/Print";

//Dialog
import HistoryDialog from "./HistoryDialog";
import OverridableFormDialog from "./OverridableFormDialog";
import FormDialog from "./FormDialog";

//material-Table
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";

import CardHeader from "../../components/CardHeader";

import handleError from "../../utils/handleError";
import { Role } from "../../utils/types";

import { setDefault } from "../../utils/pdfHelper";
import hasPrivilege from "../../utils/hasPrivilege";

import jsPDF from "jspdf";
import "jspdf-autotable";

const fieldOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true
});

// Print Order's basic details ///
const basicDetailsColumns = () => {
  return [
    { dataKey: "poRef", header: "PO Ref No" },
    { dataKey: "buyer", header: "Buyer" },
    { dataKey: "timeline", header: "Timeline" },
    { dataKey: "garmentType", header: "Season" },
    { dataKey: "season", header: "Season" },
    { dataKey: "style", header: "Style" },
    { dataKey: "orderQty", header: "Order QTY" },
    { dataKey: "orderDate", header: "Order Date" },
    { dataKey: "exFactoryDate", header: "Ex Factory Date" },
    { dataKey: "remarks", header: "Remarks" }
  ];
};

const basicDetailsData = ({ buyer, season, garmentType, orderDate, exFactoryDate, etdDate, ...order }) => {
  return [
    {
      ...order,
      buyer: buyer.name,
      season: season.name,
      garmentType: garmentType.name,
      orderDate: moment(orderDate).format("ll"),
      exFactoryDate: moment(exFactoryDate).format("ll"),
      etdDate: moment(etdDate).format("ll")
    }
  ];
};

// Print Activity details //
const activityHeader = () => {
  return [
    {
      name: "Name",
      viewLeadTime: "Lead Time",
      dueDate: "Due Date",
      completedDate: "Completed Date",
      delayReason: "Delay Reason",
      remarks: "Remarks"
    }
  ];
};

const activityData = oActivityList => {
  let oActivityListData = oActivityList
    .sort((a, b) => a.tActivity.serialNo - b.tActivity.serialNo)
    .map(({ name, dueDate, finalLeadTime, completedDate, delayReason, remarks }) => {
      return {
        name,
        dueDate: moment(dueDate).format("ll"),
        viewLeadTime: leadTime(finalLeadTime),
        completedDate: completedDate == null || completedDate === "" ? undefined : moment(completedDate).format("ll"),
        delayReason: delayReason == null ? undefined : delayReason,
        remarks: remarks == null ? undefined : remarks
      };
    });
  return oActivityListData;
};

// Print //
const printOrder = order => {
  var doc = new jsPDF();
  setDefault(doc, "Order Details for " + order.buyer.name + " #" + order.poRef);
  let startY = 10;
  doc.setFontSize(10); // Table Header size
  doc.text("Basic details", 7, startY + 7); // Table Header

  doc.autoTable({
    startY: startY + 10, // Margin from Top
    columns: basicDetailsColumns(),
    body: basicDetailsData(order),
    rowPageBreak: "auto",
    bodyStyles: { valign: "top" },
    styles: { cellWidth: "wrap", cellPadding: 2 },
    columnStyles: { remarks: { cellWidth: "auto" } }
  });

  startY = doc.autoTable.previous.finalY;
  doc.text("Activities", 7, startY + 7);
  doc.autoTable({
    startY: startY + 10,
    head: activityHeader(),
    body: activityData(order && order.oActivityList),
    rowPageBreak: "auto",
    bodyStyles: { valign: "top" },
    styles: { cellWidth: "wrap", cellPadding: 2 },
    columnStyles: { delayReason: { cellWidth: "auto" }, remarks: { cellWidth: "auto" }, viewLeadTime: { halign: "center" } }
  });
  doc.save("ORDER " + order.buyer.name + " " + order.poRef + ".pdf");
};

const leadTime = lt =>
  lt &&
  `O + ${Array(3 - `${lt}`.length)
    .fill("0")
    .join("")}${lt}`;

const renderSubActivityName = rowData =>
  rowData ? (
    <span>
      {rowData.key && rowData.key.includes("C") && <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>}
      {rowData.name}
    </span>
  ) : (
    <span />
  );

const columns = [
  { field: "name", title: "Name", render: rowData => renderSubActivityName(rowData) },
  {
    field: "viewLeadTime",
    title: "Lead Time",
    type: "numeric",
    cellStyle: { paddingRight: "2em" }
  },
  { field: "dueDate", title: "Due Date", type: "date" },
  {
    field: "compDate",
    title: "Completed Date",
    type: "date"
  },
  { field: "delayReason", title: "Delay Reasons" },
  { field: "remarks", title: "Remarks" }
];

const format = (isGrassRootUser, order) => {
  let activityList =
    order.oActivityList &&
    order.oActivityList
      .map(e => ({ ...e.tActivity, ...e }))
      .sort((a, b) => a.serialNo - b.serialNo)
      .flatMap(({ oSubActivityList, ...oActivity }) =>
        isGrassRootUser
          ? !oSubActivityList
            ? []
            : [
                {
                  ...oActivity,
                  dueDate: moment(oActivity.dueDate).format("ll"),
                  viewLeadTime: leadTime(oActivity.finalLeadTime),
                  compDate:
                    oActivity.completedDate == null || oActivity.completedDate === "" ? undefined : moment(oActivity.completedDate).format("ll"),
                  delayReason: oActivity.delayReason == null ? undefined : oActivity.delayReason,
                  remarks: oActivity.remarks == null ? undefined : oActivity.remarks,
                  key: `P-${oActivity.tActivity.id}`
                },
                ...oSubActivityList
                  .sort((c, d) => c.leadTime - d.leadTime)
                  .map(e => ({
                    ...e,
                    dueDate: moment(e.dueDate).format("ll"),
                    viewLeadTime: leadTime(oActivity.finalLeadTime + e.leadTime),
                    compDate: e.completedDate == null || e.completedDate === "" ? undefined : moment(e.completedDate).format("ll"),
                    delayReason: e.delayReason == null ? undefined : e.delayReason,
                    remarks: e.remarks == null ? undefined : e.remarks,
                    key: `C-${e.id}`,
                    parentKey: `P-${oActivity.tActivity.id}`
                  }))
              ]
          : [
              {
                ...oActivity,
                dueDate: moment(oActivity.dueDate).format("ll"),
                viewLeadTime: leadTime(oActivity.finalLeadTime),
                compDate:
                  oActivity.completedDate == null || oActivity.completedDate === "" ? undefined : moment(oActivity.completedDate).format("ll"),
                delayReason: oActivity.delayReason == null ? undefined : oActivity.delayReason,
                remarks: oActivity.remarks == null ? undefined : oActivity.remarks
              }
            ]
      );
  return activityList;
};

const styles = {
  root: {},
  content: {
    margin: "1.5em"
  },
  pageHeader: {
    "&>*": {
      backgroundColor: "rgb(255, 255, 255)",
      color: "#4a5e71",
      "&>*": {
        paddingLeft: "10px"
      },
      "&>*>*>*": {
        marginTop: "-2em",
        fontSize: "0.8rem"
      }
    }
  }
};

class OrderView extends Component {
  state = {
    dialogActive: false,
    overridableDialogActive: false,
    historyDialogActive: false,
    historyData: []
  };

  componentDidMount() {
    this.props.dispatch(crudGetOne("orders", this.props.id));
  }

  onEditSubmit = (page, rows, activity) => {
    const { order } = this.props;

    let data;
    if (page === "Activity") {
      const { oActivityList, ...rest } = order;
      const list = oActivityList.map(e => {
        const activity = rows.find(r => r.activityId === btoa(e.name));
        return activity
          ? {
              ...e,
              completedDate:
                typeof activity.completedDate === "number"
                  ? moment(activity.completedDate).format()
                  : activity.completedDate && activity.completedDate.format(),
              delayReason: Array.isArray(activity.delayReason) ? activity.delayReason.join(", ") : activity.delayReason,
              remarks: activity.remarks
            }
          : e;
      });
      data = { ...rest, oActivityList: list };
    } else if (page === "SubActivity") {
      const { oActivityList, ...rest } = order;
      const list = oActivityList.map(({ oSubActivityList, ...oActivity }) => {
        if (activity.activityId === btoa(oActivity.name)) {
          const list = oSubActivityList.map(e => {
            const subActivity = rows.find(r => r.subActivityId === btoa(e.name));
            return subActivity
              ? {
                  ...e,
                  completedDate:
                    typeof subActivity.completedDate === "number"
                      ? moment(subActivity.completedDate).format()
                      : subActivity.completedDate && subActivity.completedDate.format(),
                  remarks: subActivity.remarks
                }
              : e;
          });
          return { ...oActivity, oSubActivityList: list };
        }
        return { ...oActivity, oSubActivityList };
      });
      data = { ...rest, oActivityList: list };
    }

    const options = {
      url: "orders",
      method: "put",
      data: [data]
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });

        this.setState({ dialogActive: false, ids: [] });
        this.props.dispatch(crudGetOne("orders", this.props.id));
      })
      .catch(error => {
        console.log(error);
        handleError(error, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  // Overridable form submit
  onOverrideSubmit = values => {
    const options = {
      url: `orders/${this.props.id}`,
      method: "put",
      params: { action: "override" },
      data: values
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        if (response.status === 200 || response.status === 201) {
          this.props.dispatch(showNotification("Order updated successfully."));
          this.props.dispatch(crudGetOne("orders", this.props.id));
          this.setState({ overridableDialogActive: false });
        }
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      })
      .catch(error => {
        handleError(error, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  onHistoryClick = (type, OrderId, ActivityId) => {
    this.setState({ historyData: [type, OrderId, ActivityId], historyDialogActive: true });
  };

  render() {
    const { id, roles = [], hasAccess, order = {}, getPermissions, classes } = this.props;
    const { dialogActive, overridableDialogActive, historyDialogActive, historyData } = this.state;
    const isGrassRootUser = roles.includes(Role.MERCHANT) || roles.includes(Role.USER);
    const activityList = format(isGrassRootUser, order);
    const departmentId = getPermissions && getPermissions("departmentId") && getPermissions("departmentId")[0];
    return (
      <div className={classes.root}>
        <PageHeader title="Order View" />
        <FormDialog
          open={dialogActive}
          data={{ [id]: order }}
          ids={[id]}
          onClose={_ => this.setState({ dialogActive: false })}
          onSubmit={this.onEditSubmit}
          departmentId={departmentId}
        />
        <OverridableFormDialog
          open={overridableDialogActive}
          data={order}
          id={id}
          onClose={_ => this.setState({ overridableDialogActive: false })}
          onSubmit={this.onOverrideSubmit}
        />
        {historyDialogActive && (
          <HistoryDialog open={historyDialogActive} data={historyData} onClose={_ => this.setState({ historyDialogActive: false })} />
        )}
        <div className={classes.content}>
          <Card>
            <CardHeader title="Basic Details" />
            <Divider />
            <SimpleShowLayout style={{ padding: "1.5em" }} footer={false} record={order}>
              <TextField source="poRef" label="PO Reference" {...fieldOptions(3)} />
              <TextField source="buyer.name" label="Buyer" {...fieldOptions(3)} />
              <TextField source="timeline" {...fieldOptions(3)} />
              <TextField source="garmentType.name" label="Garment Type" {...fieldOptions(3)} />
              <TextField source="season.name" label="Season" {...fieldOptions(3)} />
              <TextField source="style" label="Style" {...fieldOptions(3)} />
              <NumberField source="orderQty" label="Order Quantity" {...fieldOptions(3)} />
              <FunctionField source="orderDate" label="Order Date" render={record => moment(record.orderDate).format("ll")} {...fieldOptions(3)} />
              {!roles.includes(Role.USER) && (
                <FunctionField
                  label="Ex Factory Date"
                  render={record => record.exFactoryDate && moment(record.exFactoryDate).format("ll")}
                  {...fieldOptions(3)}
                />
              )}
              {!roles.includes(Role.USER) && (
                <FunctionField label="ETD Date" render={record => record.etdDate && moment(record.etdDate).format("ll")} {...fieldOptions(3)} />
              )}
              <TextField source="remarks" {...fieldOptions(9)} />
            </SimpleShowLayout>
          </Card>
          <Card style={{ marginTop: "2em" }}>
            <CardHeader title="Activities" />
            <Divider />
            {activityList && activityList.length && (
              <MaterialTable
                columns={columns}
                data={activityList}
                icons={Icons}
                onTreeExpandChange={this.onTreeExpandChanged}
                parentChildData={
                  !isGrassRootUser
                    ? null
                    : (row, rows) =>
                        rows.find(a => {
                          return a.key === row.parentKey;
                        })
                }
                actions={[
                  {
                    icon: HistoryIcon,
                    onClick: (event, rowData) => this.onHistoryClick("Activity", Number(id), Number(rowData.id))
                  }
                ]}
                options={{
                  selection: false,
                  search: false,
                  paging: false,
                  header: true,
                  toolbar: false,
                  actionsColumnIndex: -1
                }}
                style={{
                  boxShadow: "none",
                  width: "100%"
                }}
                localization={{
                  header: {
                    actions: ""
                  }
                }}
                components={{
                  Action: props => {
                    return props.data && props.data.tActivity && hasPrivilege(roles, hasAccess) ? (
                      <Button
                        showLabel={false}
                        label="Activity History"
                        style={{ cursor: "pointer" }}
                        onClick={e => props.action.onClick(e, props.data)}
                      >
                        <HistoryIcon />
                      </Button>
                    ) : null;
                  }
                }}
              />
            )}
          </Card>
          <PageFooter>
            <BackButton variant="contained" style={{ marginRight: "1.5em" }} />
            {hasPrivilege(roles, hasAccess) && (
              <Button label="History" variant="contained" style={{ marginRight: "1.5em" }} onClick={_ => this.onHistoryClick("Order", Number(id))}>
                <HistoryIcon />
              </Button>
            )}
            {hasAccess && hasAccess("order", "update", "activity") && (
              <Button
                label="Update Activity"
                variant="contained"
                color="primary"
                style={{ marginRight: "1.5em" }}
                onClick={_ => this.setState({ dialogActive: true })}
              >
                <EditIcon />
              </Button>
            )}
            {hasAccess && hasAccess("order", "update", "override") && (
              <Button
                label="Override"
                variant="contained"
                color="primary"
                style={{ marginRight: "1.5em" }}
                onClick={_ => this.setState({ overridableDialogActive: true })}
              >
                <EditIcon />
              </Button>
            )}
            <Button label="Print" variant="contained" color="primary" onClick={_ => printOrder(order)}>
              <PrintIcon />
            </Button>
          </PageFooter>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state, props) => ({
  order: state.jazasoft.resources["orders"] && state.jazasoft.resources["orders"].data && state.jazasoft.resources["orders"].data[props.id]
});

export default connect(mapStateToProps)(withStyles(styles)(OrderView));
