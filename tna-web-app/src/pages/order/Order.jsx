import React from "react";
import { withStyles } from "@material-ui/styles";
import moment from "moment";

import ListIcon from "mdi-material-ui/FormatListBulletedSquare";
import GridIcon from "mdi-material-ui/ViewGrid";
import EditIcon from "@material-ui/icons/Edit";
import Divider from "@material-ui/core/Divider";

// perfect scroll bar
import "react-perfect-scrollbar/dist/css/styles.css";
import PerfectScrollbar from "react-perfect-scrollbar";

import {
  SelectInput,
  ReferenceInput,
  List,
  Datagrid,
  TextField,
  Button,
  CreateButton,
  ShowButton,
  EditButton,
  Filter,
  DeleteButton,
  FunctionField,
  FilterButton
} from "jazasoft";
import Table, { CheckBox, Toolbar } from "jazasoft/lib/mui/components/Table";

import FormDialog from "./FormDialog";

import hasPrivilege from "../../utils/hasPrivilege";

const filters = (
  <Filter
    parse={({ buyerId }) => ({
      "buyer.id": buyerId
    })}
  >
    <ReferenceInput
      source="buyerId"
      reference="buyers"
      resource="buyers"
      sort={{ field: "name", order: "asc" }}
      xs={12}
      fullWidth={true}
      options={{ fullWidth: true }}
    >
      <SelectInput optionText="name" />
    </ReferenceInput>
  </Filter>
);

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
    ? orderList.flatMap(order => (order.oActivityList ? order.oActivityList.map(({ id, serialNo, name }) => ({ id, serialNo, name })) : []))
    : [];
  aList.forEach(activity => {
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
    view: "list", // list, grid
    dialogActive: false
  };

  onViwSwitch = () => {
    this.setState({ view: this.state.view === "list" ? "grid" : "list" });
  };

  onEditClick = (data, ids) => e => {
    this.setState({ dialogActive: true });
  };

  onClose = () => {
    this.setState({ dialogActive: false });
  };

  render() {
    const { roles, hasAccess, classes, ...props } = this.props;
    const { view, dialogActive } = this.state;
    return (
      <div>
        <FormDialog open={dialogActive} onClose={this.onClose} />
        <List
          filters={filters}
          key={view}
          searchKeys={["poRef", "style"]}
          actions={({ basePath }) => (
            <div>
              {hasPrivilege(roles, hasAccess, "order", "write") && <CreateButton basePath={basePath} showLabel={false} />}
              <FilterButton showLabel={false} />
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

const StyledOrder = withStyles(styles)(Order);

export default StyledOrder;
