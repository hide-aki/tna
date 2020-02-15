import React, { Component } from "react";

import { Create, SimpleForm, FormDataConsumer, TextInput, NumberInput, DateInput, ReferenceInput, SelectInput, required, minLength } from "jazasoft";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

class OrederCreate extends Component {
  render() {
    const { ...props } = this.props;
    return (
      <Create {...props}>
        <SimpleForm redirect="home">
          <ReferenceInput source="buyerId" reference="buyers" validate={required()} {...inputOptions(3)}>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <FormDataConsumer {...inputOptions(3)}>
            {({ formData }) => {
              return (
                <ReferenceInput
                  source="timelineId"
                  reference="timelines"
                  filter={{ "buyer.id": formData.buyerId || "-1" }}
                  validate={required()}
                  {...inputOptions(3)}
                >
                  <SelectInput optionText="name" />
                </ReferenceInput>
              );
            }}
          </FormDataConsumer>

          <ReferenceInput source="garmentTypeId" reference="garmentTypes" validate={required()} {...inputOptions(3)}>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <ReferenceInput source="seasonId" reference="seasons" validate={required()} {...inputOptions(3)}>
            <SelectInput optionText="name" />
          </ReferenceInput>
          <TextInput source="poRef" label="PO Reference" validate={[required(), minLength(2)]} {...inputOptions(3)} />
          <NumberInput source="orderQty" validate={required()} {...inputOptions(3)} />
          <TextInput source="style" validate={[required(), minLength(2)]} {...inputOptions(3)} />
          <TextInput source="remarks" label="Remarks" validate={[minLength(2)]} {...inputOptions(3)} />
          <DateInput source="orderDate" validate={required()} {...inputOptions(3)} />
          <DateInput label="EX Factory Date" source="exFactoryDate" validate={required()} {...inputOptions(3)} />
          <DateInput label="ETD Date" source="etdDate" validate={required()} {...inputOptions(3)} />
        </SimpleForm>
      </Create>
    );
  }
}

export default OrederCreate;
