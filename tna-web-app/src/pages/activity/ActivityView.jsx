import React, { Component } from "react";

import {
  FunctionField,
  Show,
  MultiCardShowLayout,
  ShowCard,
  TextField,
  Datagrid,
  ReferenceField,
  ReferenceArrayField,
  SingleFieldList,
  ChipField
} from "jazasoft";

class ActivityView extends Component {
  format = record => {
    if (record && record.notify !== null) {
      const { notify, ...rest } = record;
      return { ...rest, notify: notify.split(",") };
    } else {
      return record;
    }
  };

  render() {
    const { classes, ...props } = this.props;

    return (
      <Show format={this.format} cardWrapper={false} {...props}>
        <MultiCardShowLayout>
          <ShowCard title="Activity Details">
            <TextField source="serialNo" />
            <TextField source="name" />
            <ReferenceField source="departmentId" reference="departments">
              <TextField source="name" />
            </ReferenceField>

            <ReferenceArrayField
              label="Notify Departments"
              reference="departments"
              source="notify"
            >
              <SingleFieldList>
                <ChipField allowEmpty={true} source="name" />
              </SingleFieldList>
            </ReferenceArrayField>

            <FunctionField
              label="C-Level"
              render={record => (record.cLevel ? "Yes" : "No")}
            />
            <FunctionField
              label="Default Activity"
              render={record => (record.isDefault ? "Yes" : "No")}
            />
            <TextField source="delayReason" label="Delay Reason" />
          </ShowCard>
          <ShowCard
            title="Subactivities"
            content={({ record = {} }) => {
              const data = record.subActivityList
                ? record.subActivityList.reduce(
                    (acc, el) => ({ ...acc, [el.id]: el }),
                    {}
                  )
                : {};
              const ids = record.subActivityList
                ? record.subActivityList.map(e => e.id)
                : [];
              return (
                <Datagrid data={data} ids={ids}>
                  <TextField source="name" />
                  <TextField source="desc" />
                </Datagrid>
              );
            }}
          />
        </MultiCardShowLayout>
      </Show>
    );
  }
}

export default ActivityView;
