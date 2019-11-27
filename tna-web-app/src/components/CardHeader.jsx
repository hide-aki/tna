import React from "react";

import MuiCardHeader from "@material-ui/core/CardHeader";

export default ({ style, ...props }) => (
  <MuiCardHeader
    style={{ marginLeft: 12, fontSize: 14, color: "#3F51B5", ...style }}
    disableTypography={true}
    {...props}
  />
);
