import React from 'react';
import { withStyles } from "@material-ui/styles"
import {
    List,
  Datagrid,
  TextField,
  ReferenceField,
  ShowButton,
  EditButton,
  DeleteButton
} from "jazasoft"

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
          <TextField source="poRef" label="PO Reference"/>
          <TextField source="orderQty" label="Order Quantity" />
          <TextField source="style" label="Style"/>
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