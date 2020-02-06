import React from "react";
import { withStyles } from "@material-ui/styles";
import {
  List,
  Datagrid,
  TextField,
  ReferenceField,
  ShowButton,
  EditButton,
  DeleteButton,
  FunctionField
} from "jazasoft";

import moment from "moment";
import hasPrivilege from "../../utils/hasPrivilege";

const homeStyle = theme => ({
  button: {
    width: theme.spacing(2)
  }
});

export default withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;
  return (
    <List {...props}>
      <Datagrid>
        <ReferenceField source="buyerId" reference="buyers">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="poRef" label="PO Reference" />
        <TextField source="orderQty" label="Order Quantity" />
        <TextField source="style" label="Style" />
        <FunctionField
          source="orderDate"
          label="Order Date"
          render={record => moment(record.orderDate).format("ll")}
        />
        <FunctionField
          source="exFactoryDate"
          label="Ex-factory Date"
          render={record => moment(record.exFactoryDate).format("ll")}
        />
        <ShowButton cellClassName={classes.button} />
        {hasPrivilege(roles, hasAccess, "buyer", "update") && (
          <EditButton cellClassName={classes.button} />
        )}
        {hasPrivilege(roles, hasAccess, "buyer", "delete") && (
          <DeleteButton cellClassName={classes.button} />
        )}
      </Datagrid>
    </List>
  );
});
