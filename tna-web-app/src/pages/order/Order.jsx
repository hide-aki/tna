import React from "react";
import { withStyles } from "@material-ui/styles";
import moment from "moment";
import { connect } from "react-redux";

import ListIcon from "mdi-material-ui/FormatListBulletedSquare";
import GridIcon from "mdi-material-ui/ViewGrid";
import EditIcon from "@material-ui/icons/Edit";
import Divider from "@material-ui/core/Divider";

// perfect scroll bar
import "react-perfect-scrollbar/dist/css/styles.css";
import PerfectScrollbar from "react-perfect-scrollbar";

import {
  List,
  Datagrid,
  TextField,
  Button,
  CreateButton,
  ShowButton,
  EditButton,
  DeleteButton,
  FunctionField,
  crudGetMany,
  RestMethods,
  FETCH_START,
  FETCH_END,
  SAVING_START,
  SAVING_END
} from "jazasoft";
import Table, { CheckBox, Toolbar } from "jazasoft/lib/mui/components/Table";

import FormDialog from "./FormDialog";

import hasPrivilege from "../../utils/hasPrivilege";
import handleError from "../../utils/handleError";
import { dataProvider } from "../../App";

const MyTextField = ({ column, record }) => {
  const activity = record[column.dataKey];
  let value, bgColor;
  if (activity) {
    let dueDate;
    if (activity.timeFrom === "O") {
      dueDate = moment(record.orderDate).add(activity.leadTime, "days");
    } else if (activity.timeFrom === "E") {
      dueDate = moment(record.exFactoryDate).subtract(activity.leadTime, "days");
    }
    value = activity.completedDate ? moment(activity.completedDate).format("ll") : dueDate ? dueDate.format("ll") : null;
    bgColor = "yellow";
  }
  return (
    <div style={{ display: "flex", justifyContent: "center" }}>
      <span
        style={{
          backgroundColor: bgColor,
          display: "block",
          // width: 30,
          padding: "3px 3px",
          textAlign: "center"
        }}
      >
        {value}
      </span>
    </div>
  );
};

const getColumns = (orderList, onChange) => {
  let activityList = [];
  let activitySet = new Set();
  const aList = orderList
    ? orderList.flatMap(order =>
        order.oActivityList ? order.oActivityList.map(({ serialNo, name, tActivity }) => ({ serialNo: tActivity.serialNo, name })) : []
      )
    : [];
  aList
    .sort((a, b) => (a.serialNo ? -1 : 1))
    .forEach(activity => {
      if (!activitySet.has(activity.name)) {
        activitySet.add(activity.name);
        activityList.push(activity);
      }
    });
  let columns = [
    { dataKey: "selector", element: <CheckBox onChange={onChange} /> },
    { dataKey: "edit" },
    { dataKey: "poRef", title: "PO Ref. No" },
    { dataKey: "buyerName", title: "Buyer" },
    { dataKey: "seasonName", title: "Season" },
    { dataKey: "style", title: "Style" },
    { dataKey: "orderQty", title: "Order Qty" },
    { dataKey: "exFactory", title: "Ex Factory" },
    ...activityList.sort((a, b) => a.serialNo - b.serialNo).map(({ name }) => ({ dataKey: btoa(name), title: name, element: <MyTextField /> }))
  ];
  return columns;
};

const CustomDatagrid = ({ classes, view, roles, hasAccess, onEditClick, ...props }) => {
  const [selectedIds, setSelectedIds] = React.useState([]);

  const onChange = ({ record, column }) => event => {
    let ids = selectedIds.slice();
    const dataKey = column.dataKey;
    if (dataKey === "selector") {
      const id = record.id;
      if (event.target.checked) {
        ids.push(id);
      } else {
        ids = ids.filter(e => e !== id);
      }
    }
    setSelectedIds(ids);
  };

  const { data, ids } = props;
  let rows = [];
  if (view === "grid") {
    rows = [...new Set([...selectedIds, ...ids])]
      .map(id => data[id])
      .map(e => {
        const activityList = e.oActivityList || [];
        return {
          ...e,
          selector: selectedIds.includes(e.id),
          edit: (
            <div>
              <EditIcon onClick={onEditClick && onEditClick(data, [e.id])} />
            </div>
          ),
          buyerName: e.buyer && e.buyer.name,
          seasonName: e.season && e.season.name,
          exFactory: moment(e.exFactory).format("ll"),
          ...activityList.reduce(
            (acc, activity) => ({
              ...acc,
              [btoa(activity.name)]: activity
            }),
            {}
          )
        };
      });
  }
  return view === "list" ? (
    <Datagrid {...props}>
      <TextField source="poRef" label="PO Reference" />
      <TextField label="Buyer" source="buyer.name" />
      <TextField source="orderQty" label="Order Quantity" />
      <TextField source="style" label="Style" />
      <FunctionField source="orderDate" label="Order Date" render={record => moment(record.orderDate).format("ll")} />
      <FunctionField source="exFactoryDate" label="Ex-factory Date" render={record => moment(record.exFactoryDate).format("ll")} />
      <ShowButton cellClassName={classes.button} />
      {hasPrivilege(roles, hasAccess, "buyer", "update") && <EditButton cellClassName={classes.button} />}
      {hasPrivilege(roles, hasAccess, "buyer", "delete") && <DeleteButton cellClassName={classes.button} />}
    </Datagrid>
  ) : (
    <div>
      {selectedIds.length > 0 && (
        <React.Fragment>
          <Toolbar
            title={`${selectedIds.length} Item${selectedIds.length > 1 ? "s" : ""} selected`}
            actions={[{ name: "edit", tooltip: "Update Completed Date", onClick: onEditClick && onEditClick(data, selectedIds) }]}
          />
          <Divider />
        </React.Fragment>
      )}
      <PerfectScrollbar>
        <Table classes={{ table: classes.nowrapTable }} columns={getColumns(rows, onChange)} rows={rows} />
      </PerfectScrollbar>
    </div>
  );
};

const styles = theme => ({
  button: {
    width: theme.spacing(2)
  },
  nowrapTable: {
    whiteSpace: "nowrap"
  }
});

class Order extends React.Component {
  state = {
    view: "grid", // list, grid
    dialogActive: false,
    ids: []
  };

  onViwSwitch = () => {
    this.setState({ view: this.state.view === "list" ? "grid" : "list" });
  };

  onEditSubmit = (page, rows, activity) => {
    const { orders } = this.props;
    const { ids } = this.state;

    let orderList = ids.map(id => orders[id]);
    if (page === "Activity") {
      orderList = orderList.map(({ oActivityList, ...order }) => {
        const list = oActivityList.map(e => {
          const activity = rows.find(r => r.activityId === btoa(e.name));
          return activity
            ? {
                ...e,
                completedDate: activity.completedDate && activity.completedDate.format(),
                delayReason: Array.isArray(activity.delayReason) ? activity.delayReason.join(", ") : activity.delayReason,
                remarks: activity.remarks
              }
            : e;
        });
        return { ...order, oActivityList: list };
      });
    } else if (page === "SubActivity") {
      orderList = orderList.map(({ oActivityList, ...order }) => {
        const list = oActivityList.map(({ oSubActivityList, ...oActivity }) => {
          if (activity.activityId === btoa(oActivity.name)) {
            const list = oSubActivityList.map(e => {
              const subActivity = rows.find(r => r.subActivityId === btoa(e.name));
              return subActivity
                ? {
                    ...e,
                    completedDate: subActivity.completedDate && subActivity.completedDate.format(),

                    remarks: subActivity.remarks
                  }
                : e;
            });
            return { ...oActivity, oSubActivityList: list };
          }
          return { ...oActivity, oSubActivityList };
        });
        return { ...order, oActivityList: list };
      });
    }

    const options = {
      url: "orders",
      method: "put",
      data: orderList
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });

        this.setState({ dialogActive: false, ids: [] });
        this.props.dispatch(crudGetMany("orders", ids, undefined, { params: { view: "grid" } }));
      })
      .catch(error => {
        console.log(error);
        handleError(error, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  onEditClick = (data, ids) => e => {
    this.props.dispatch(crudGetMany("orders", ids, undefined, { params: { view: "grid-deep" } }));
    this.setState({ dialogActive: true, ids });
  };

  onClose = () => {
    this.setState({ dialogActive: false });
  };

  render() {
    const { classes, orders, dispatch, ...props } = this.props;
    const { view, dialogActive, ids } = this.state;
    const { roles, hasAccess } = props;
    return (
      <div>
        <FormDialog open={dialogActive} data={orders} ids={ids} onClose={this.onClose} onSubmit={this.onEditSubmit} />
        <List
          key={view}
          searchKeys={["poRef", "style"]}
          actions={({ basePath }) => (
            <div>
              <CreateButton basePath={basePath} showLabel={false} />
              <Button showLabel={false} label="View" onClick={this.onViwSwitch}>
                {view === "list" ? <ListIcon /> : <GridIcon />}
              </Button>
            </div>
          )}
          requestConfig={{ params: { view } }}
          {...props}
        >
          <CustomDatagrid classes={classes} view={view} roles={roles} hasAccess={hasAccess} onEditClick={this.onEditClick} />
        </List>
      </div>
    );
  }
}

const mapStateToProps = (state, props) => ({ orders: state.jazasoft.resources[props.resource] && state.jazasoft.resources[props.resource].data });

const StyledOrder = withStyles(styles)(Order);

export default connect(mapStateToProps)(StyledOrder);
