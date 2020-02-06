import React, { Component } from "react";

import {
  Show,
  TextField,
  ShowCard,
  MultiCardShowLayout,
  ReferenceField
} from "jazasoft";

import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";

const grassRoot = true;

const activityColumns = [
  { field: "name", title: "Name" },
  { field: "leadTime", title: "Lead Time" }
];

class TimelineView extends Component {
  onTreeExpandChange = (element, isExpanded) => {};

  render() {
    const { classes, tActivityList, ...props } = this.props;
    return (
      <div>
        <Show cardWrapper={false} {...props}>
          <MultiCardShowLayout>
            <ShowCard title="Timeline Details">
              <ReferenceField source="buyerId" reference="buyers">
                <TextField source="name" />
              </ReferenceField>
              <TextField source="name" />
              <TextField source="tnaType" label="TNA Type" />
            </ShowCard>

            <ShowCard
              title="Activities"
              content={({ record = {} }) => {
                if (!record.tActivityList) return null;
                let data = grassRoot
                  ? record.tActivityList
                      .sort((a, b) => a.activity.serialNo - b.activity.serialNo)
                      .flatMap(({ tSubActivityList, ...tActivity }) => {
                        return [
                          {
                            ...tActivity,
                            name: tActivity.activity && tActivity.activity.name
                          },
                          ...tSubActivityList.map(e => ({
                            ...e,
                            id: null,
                            name: e.subActivity && e.subActivity.name,
                            timeFrom: tActivity.timeFrom
                          }))
                        ];
                      })
                  : record.tActivityList;
                data = data.map(e => ({
                  ...e,
                  name: e.name ? e.name : e.activity.name,
                  leadTime:
                    e.timeFrom === "O"
                      ? `O + ` + e.leadTime
                      : `E - ` + e.leadTime
                }));

                return (
                  <MaterialTable
                    columns={activityColumns}
                    data={data}
                    icons={Icons}
                    options={{
                      toolbar: false,
                      search: false,
                      paging: false,
                      header: true
                    }}
                    style={{
                      boxShadow: "none",
                      width: "100%"
                    }}
                    parentChildData={
                      !grassRoot
                        ? null
                        : (row, rows) =>
                            rows.find(a => {
                              return a.id === row.tActivityId;
                            })
                    }
                    onTreeExpandChange={this.onTreeExpandChange}
                  />
                );
              }}
            />
          </MultiCardShowLayout>
        </Show>
      </div>
    );
  }
}

export default TimelineView;
