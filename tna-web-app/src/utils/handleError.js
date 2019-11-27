import { showNotification } from "jazasoft";
export default (error, dispatch) => {
  if (error && error.message) {
    dispatch(
      showNotification(error.message, {
        type: "danger",
        placement: "tc",
        autoHideDuration: 10000
      })
    );
  } else {
    console.log(error);
  }
};
