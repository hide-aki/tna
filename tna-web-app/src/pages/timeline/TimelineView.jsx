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
  { field: "leadTimeNormal", title: "Lead Time Normal" },
  { field: "leadTimeOptimal", title: "Lead Time Optimal" }
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
                  ? record.tActivityList.flatMap(
                      ({ tSubActivityList, ...tActivity }) => {
                        return [
                          {
                            ...tActivity,
                            name: tActivity.activity && tActivity.activity.name
                          },
                          ...tSubActivityList.map(e => ({
                            ...e,
                            name: e.subActivity && e.subActivity.name,
                            timeFrom: tActivity.timeFrom
                          }))
                        ];
                      }
                    )
                  : record.tActivityList;
                data = data.map(e => ({
                  ...e,
                  name: e.name,
                  leadTimeNormal:
                    e.timeFrom === "O"
                      ? `O + ` + e.leadTimeNormal
                      : `E - ` + e.leadTimeNormal,
                  leadTimeOptimal: !e.leadTimeOptimal
                    ? ""
                    : e.timeFrom === "O"
                    ? `O + ` + e.leadTimeOptimal
                    : `E - ` + e.leadTimeOptimal
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
                            rows.find(a => a.id === row.tActivityId)
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
