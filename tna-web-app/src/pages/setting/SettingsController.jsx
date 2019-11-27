import React from "react";
import PropTypes from "prop-types";

import { connect } from "react-redux";

import { RestMethods, FETCH_START, FETCH_END } from "jazasoft";

import { dataProvider } from "../../App";

class SettingsController extends React.Component {
  state = {
    groups: [],
    settings: []
  };

  componentDidMount() {
    this.fetchSettings();
  }

  fetchSettings = (action = "default") => {
    const options = {
      url: 'settings',
      method: "get",
      params: {action}
    };
    this.props.dispatch({ type: FETCH_START });

    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 200) {
          this.setState({ ...response.data });
        }
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        this.props.dispatch({ type: FETCH_END });
      });
  };

  updateSetting = () => {};

  render() {
    const { children } = this.props;

    return children({
      fetchSettings: this.fetchSettings,
      updateSetting: this.updateSetting,
      ...this.state
    });
  }
}

SettingsController.propTypes = {
  children: PropTypes.func.isRequired
};

export default connect()(SettingsController);
