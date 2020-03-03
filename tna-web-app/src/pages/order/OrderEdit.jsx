import React, { Component } from "react";

import { Edit, SimpleForm, TextInput, NumberInput, DateInput, ReferenceInput, SelectInput, required, minLength } from "jazasoft";
import Forbidden from "../../components/Forbidden";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

class OrderEdit extends Component {
  render() {
    const { hasAccess } = this.props;
    if (!hasAccess || !hasAccess("order", "update", "default")) {
      return <Forbidden history={this.props.history} />;
    }
    return (
      <Edit {...this.props}>
        <SimpleForm redirect="home">
          <ReferenceInput source="buyerId" reference="buyers" validate={required()} {...inputOptions(3)} disabled>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <ReferenceInput source="garmentTypeId" reference="garmentTypes" validate={required()} {...inputOptions(3)}>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <ReferenceInput source="seasonId" reference="seasons" validate={required()} {...inputOptions(3)}>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <TextInput source="poRef" label="PO Reference" validate={[required(), minLength(2)]} {...inputOptions(3)} />
          <NumberInput source="orderQty" validate={required()} {...inputOptions(3)} />
          <TextInput source="style" validate={[required(), minLength(2)]} {...inputOptions(3)} />
          <DateInput source="orderDate" validate={required()} {...inputOptions(3)} />
          <DateInput source="exFactoryDate" validate={required()} {...inputOptions(3)} />
          <DateInput label="ETD Date" source="etdDate" {...inputOptions(3)} />
          <TextInput source="remarks" label="Remarks" validate={[minLength(2)]} {...inputOptions(9)} />
        </SimpleForm>
      </Edit>
    );
  }
}

export default OrderEdit;
