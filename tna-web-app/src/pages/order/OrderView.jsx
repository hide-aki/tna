import React, { Component } from "react";
import { connect } from "react-redux";
import {
  TextField,
  NumberField,
  SimpleShowLayout,
  ReferenceField,
  crudGetOne,
  PageFooter,
  BackButton,
  FunctionField,
  SAVING_START,
  RestMethods,
  SAVING_END,
  FETCH_START,
  FETCH_END,
  PageHeader
} from "jazasoft";

import isEqual from "lodash/isEqual";
import moment from "moment";

import { dataProvider } from "../../App";

//Material-UI
import Divider from "@material-ui/core/Divider";
import Paper from "@material-ui/core/Paper";
import withStyles from "@material-ui/styles/withStyles";

//material-Table
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";

const styles = {
  root: {
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

const grassRoot = true;

const activityColumns = [
  { field: "name", title: "Name", editable: "never" },
  {
    field: "viewLeadTime",
    title: "Lead Time",
    type: "numeric",
    editable: "never"
  },
  { field: "viewDueDate", title: "Due Date", type: "date", editable: "never" },
  {
    field: "compDate",
    title: "Completed Date",
    type: "date",
    editable: "onUpdate"
  },
  { field: "delayReason", title: "Delay Reason", editable: "onUpdate" },
  { field: "remarks", title: "Remarks", editable: "onUpdate" }
];

const format = order => {
  let orderView =
    order.oActivityList &&
    order.oActivityList.flatMap(({ oSubActivityList, ...oActivity }) => [
      {
        ...oActivity,
        viewDueDate:
          oActivity.timeFrom === "O"
            ? moment(order.orderDate)
                .add(oActivity.leadTime, "day")
                .format("ll")
            : moment(order.exFactoryDate)
                .subtract(oActivity.leadTime, "day")
                .format("ll"),
        compDate: oActivity.completedDate == null || oActivity.completedDate === "" ? undefined : moment(oActivity.completedDate).format("ll"),
        delayReason: oActivity.delayReason == null ? undefined : oActivity.delayReason,
        remarks: oActivity.remarks == null ? undefined : oActivity.remarks,
        actId: oActivity.id
      },
      ...oSubActivityList.map(e => ({
        ...e,
        viewDueDate:
          oActivity.timeFrom === "O"
            ? moment(order.orderDate)
                .add(e.leadTime, "day")
                .format("ll")
            : moment(order.exFactoryDate)
                .subtract(e.leadTime, "day")
                .format("ll"),
        compDate: e.completedDate == null || e.completedDate === "" ? undefined : moment(e.completedDate).format("ll"),
        delayReason: e.delayReason == null ? undefined : e.delayReason,
        remarks: e.remarks == null ? undefined : e.remarks
      }))
    ]);
  return orderView;
};

class OrderView extends Component {
  state = {
    initialValues: {}
  };

  componentDidMount() {
    this.init();
    this.props.dispatch(crudGetOne("orders", this.props.id));
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.order, nextProps.order)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { order } = props;
    if (!order) return;
    let initialValues = grassRoot ? format(order) : order.oActivityList;
    initialValues =
      initialValues &&
      initialValues.map(e => ({
        ...e,
        viewLeadTime: e.timeFrom === "O" ? `O + ` + e.leadTime : `E - ` + e.leadTime
      }));
    this.setState({ initialValues });
  };

  onRowUpdate = async (newData, oldData) => {
    let { delayReason, compDate, remarks, ...rest } = newData;
    let data = {
      ...rest,
      delayReason: delayReason && delayReason.toString().trim(),
      remarks: remarks && remarks.toString().trim(),
      completedDate:
        typeof compDate === "string"
          ? moment(compDate, "ll").format()
          : compDate !== null && typeof compDate == "object"
          ? moment(compDate).format()
          : compDate
    };

    const parsedData = data;
    // if ("name" in parsedData) {
    //   this.updateActivity(parsedData);
    // } else if ("nam" in parsedData) {
    //   this.updateSubActivity(parsedData);
    // }
  };

  updateActivity = oActivity => {
    const options = {
      url: `orders/${this.props.id}/activities/${oActivity.id}`,
      method: "put",
      data: oActivity
    };
    this.updateAndFetch(options);
  };

  updateSubActivity = oSubActivity => {
    const options = {
      url: `orders/${this.props.id}/activities/${oSubActivity.oActivityId}/subActivities/${oSubActivity.id}`,
      method: "put",
      data: oSubActivity
    };
    this.updateAndFetch(options);
  };

  updateAndFetch = options => {
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
        this.props.dispatch(crudGetOne("orders", this.props.id));
      })
      .catch(error => {
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  render() {
    const { order = {}, classes } = this.props;
    const { initialValues } = this.state;
    return (
      <div className={classes.root}>
        <PageHeader title="Order View" />
        <Paper style={{ padding: "1.5em" }}>
          <PageHeader title="Basic Details" className={classes.pageHeader} />
          <Divider style={{ marginTop: "-2em", marginBottom: "3em" }} />
          <SimpleShowLayout footer={false} record={order}>
            <ReferenceField source="buyerId" reference="buyers" allowEmpty={true}>
              <TextField source="name" />
            </ReferenceField>
            <TextField source="poRef" label="PO Reference" />
            <ReferenceField source="garmentTypeId" label="Garment Type" reference="garmentTypes" allowEmpty={true}>
              <TextField source="name" />
            </ReferenceField>
            <ReferenceField source="seasonId" reference="seasons" allowEmpty={true}>
              <TextField source="name" />
            </ReferenceField>
            <TextField source="style" label="Style" />
            <NumberField source="orderQty" label="Order Quantity" />
            <TextField source="remarks" />
            <FunctionField source="orderDate" label="Order Date" render={record => moment(record.orderDate).format("ll")} />
            <FunctionField source="exFactoryDate" label="Ex Factory Date" render={record => moment(record.exFactoryDate).format("ll")} />
          </SimpleShowLayout>
        </Paper>
        <Paper style={{ padding: "1.5em", marginTop: "2em" }}>
          <PageHeader title="Activities" className={classes.pageHeader} />
          <Divider style={{ marginTop: "-2em" }} />
          {initialValues && initialValues.length && (
            <MaterialTable
              columns={activityColumns}
              data={initialValues}
              parentChildData={
                !grassRoot
                  ? null
                  : (row, rows) =>
                      rows.find(a => {
                        return a.id === row.oActivityId;
                      })
              }
              options={{
                selection: false,
                search: false,
                paging: false,
                header: true,
                toolbar: false,
                actionsColumnIndex: -1
              }}
              icons={Icons}
              style={{
                boxShadow: "none",
                width: "100%"
              }}
              editable={{
                onRowUpdate: this.onRowUpdate
              }}
            />
          )}
        </Paper>
        <PageFooter>
          <BackButton variant="contained" />
        </PageFooter>
      </div>
    );
  }
}

const mapStateToProps = (state, props) => ({
  order: state.jazasoft.resources["orders"] && state.jazasoft.resources["orders"].data && state.jazasoft.resources["orders"].data[props.id]
});

export default connect(mapStateToProps)(withStyles(styles)(OrderView));
