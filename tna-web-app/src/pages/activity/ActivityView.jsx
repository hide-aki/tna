import React, { Component } from "react";

import {
  FunctionField,
  Show,
  MultiCardShowLayout,
  ShowCard,
  TextField,
  Datagrid,
  ReferenceField
} from "jazasoft";

class ActivityView extends Component {
  render() {
    const { classes, ...props } = this.props;
    return (
      <Show cardWrapper={false} {...props}>
        <MultiCardShowLayout>
          <ShowCard title="Activity Details">
            <TextField source="serialNo" />
            <TextField source="name" />
            <ReferenceField source="departmentId" reference="departments">
              <TextField source="name" />
            </ReferenceField>
            <FunctionField
              label="C-Level"
              render={record => (record.cLevel ? "Yes" : "No")}
            />
          </ShowCard>
          <ShowCard
            title="Sub Activities"
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
