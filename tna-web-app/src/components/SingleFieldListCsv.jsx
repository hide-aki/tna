import React, { Component } from "react";
import PropTypes from "prop-types";

import TextField from "jazasoft/lib/mui/field/TextField";

class SingleFieldListCsv extends Component {
  render() {
    const { data = {}, ids = [], source, render } = this.props;
    const value = Object.keys(data)
      .map(id => data[id])
      .filter(e => ids.includes(e.id))
      .map(e => (render ? render(e) : e[source]))
      .join(", ");

    return <TextField record={{ value }} source="value" />;
  }
}

SingleFieldListCsv.propTypes = {
  data: PropTypes.object,
  ids: PropTypes.array,
  source: PropTypes.string,
  render: PropTypes.func
};

export default SingleFieldListCsv;
