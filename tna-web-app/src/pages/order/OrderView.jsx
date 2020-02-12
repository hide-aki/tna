import React, { Component } from "react";
import { connect } from "react-redux";
import {
  TextField,
  NumberField,
  SimpleShowLayout,
  ReferenceField,
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
  PageHeader
} from "jazasoft";

import isEqual from "lodash/isEqual";
import moment from "moment";

import { dataProvider } from "../../App";

//Material-UI
import Divider from "@material-ui/core/Divider";
import Card from "@material-ui/core/Card";
import withStyles from "@material-ui/styles/withStyles";

// Icons
import EditIcon from "@material-ui/icons/Edit";

//material-Table
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";

import CardHeader from "../../components/CardHeader";
import FormDialog from "./FormDialog";

import handleError from "../../utils/handleError";

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

const grassRoot = true;

const columns = [
  { field: "name", title: "Name" },
  {
    field: "viewLeadTime",
    title: "Lead Time",
    type: "numeric"
  },
  { field: "viewDueDate", title: "Due Date", type: "date" },
  {
    field: "compDate",
    title: "Completed Date",
    type: "date"
  },
  { field: "delayReason", title: "Delay Reason" },
  { field: "remarks", title: "Remarks" }
];

const format = order => {
  let orderView =
    order.oActivityList &&
    order.oActivityList
      .map(e => ({ ...e.tActivity, ...e }))
      .sort((a, b) => a.serialNo - b.serialNo)
      .flatMap(({ oSubActivityList, ...oActivity }) => [
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
    initialValues: {},
    dialogActive: false
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

  render() {
    const { id, order = {}, classes } = this.props;
    const { initialValues, dialogActive } = this.state;
    return (
      <div className={classes.root}>
        <PageHeader title="Order View" />

        <FormDialog
          open={dialogActive}
          data={{ [id]: order }}
          ids={[id]}
          onClose={_ => this.setState({ dialogActive: false })}
          onSubmit={this.onEditSubmit}
        />

        <div className={classes.content}>
          <Card>
            <CardHeader title="Basic Details" />
            <Divider />
            <SimpleShowLayout style={{ padding: "1.5em" }} footer={false} record={order}>
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
          </Card>
          <Card style={{ marginTop: "2em" }}>
            <CardHeader title="Activities" />
            <Divider />
            {initialValues && initialValues.length && (
              <MaterialTable
                columns={columns}
                data={initialValues}
                onTreeExpandChange={this.onTreeExpandChanged}
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
              />
            )}
          </Card>
          <PageFooter>
            <Button
              label="Update Activity"
              variant="contained"
              color="primary"
              style={{ marginRight: "1.5em" }}
              onClick={_ => this.setState({ dialogActive: true })}
            >
              <EditIcon />
            </Button>
            <BackButton variant="contained" />
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
