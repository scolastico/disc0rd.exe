function fireErrorSwal() {
  Swal.fire({
    type: "error",
    title: "Oops...",
    text: "Something went wrong!",
    showConfirmButton: false,
    allowEscapeKey: false,
    allowEnterKey: false,
    allowOutsideClick: false,
  });
}

function fireSwal(title, description, icon) {
  Swal.fire({ type: icon, title: title, text: description });
}

function fireToast(icon, text) {
  const Toast = Swal.mixin({
    toast: true,
    position: "top-end",
    showConfirmButton: false,
    timer: 3000,
    onOpen: (toast) => {
      toast.addEventListener("mouseenter", Swal.stopTimer);
      toast.addEventListener("mouseleave", Swal.resumeTimer);
    },
  });
  Toast.fire({ type: icon, title: text });
}

function fireSuccessToast() {
  fireToast("success", "Success!");
}

try {
  function clickOnAnything(id) {
    let element = document.getElementById(id + "-warning");
    if (element.classList.contains("invisible")) {
      element.classList.remove("invisible");
    }
  }

  function create() {
    if (document.getElementById("create-card")) {
      fireToast(
        "error",
        "Please save the group first before you create another one."
      );
    } else {
      const xmlHttp1 = new XMLHttpRequest();
      xmlHttp1.open("GET", "../api/v1/guild/permissions/info", true);
      xmlHttp1.onreadystatechange = function () {
        try {
          if (xmlHttp1.readyState === 4 && xmlHttp1.status === 200) {
            const info = JSON.parse(xmlHttp1.responseText);
            if (info.status === "ok") {
              let availablePermissions = info.availablePermissions;
              let defaultValues = info.defaultValues;
              document.getElementById(
                "main-container"
              ).innerHTML += getHtmlPermissionsCard(
                "create",
                defaultValues,
                availablePermissions,
                "0",
                false
              );
              clickOnAnything("create");
              fireToast("success", "Created!");
              document.getElementById("create-card").scrollIntoView({
                behavior: "smooth",
                block: "start",
              });
            } else {
              fireErrorSwal();
            }
          }
        } catch (e) {
          fireErrorSwal();
          throw e;
        }
      };
      xmlHttp1.send();
    }
  }

  function save(id) {
    try {
      const xmlHttp1 = new XMLHttpRequest();
      xmlHttp1.open("GET", "../api/v1/guild/permissions/info", true);
      xmlHttp1.onreadystatechange = function () {
        try {
          if (xmlHttp1.readyState === 4 && xmlHttp1.status === 200) {
            const info = JSON.parse(xmlHttp1.responseText);
            if (info.status === "ok") {
              let availablePermissions = info.availablePermissions;
              let permissions = {};
              for (const [permission, description] of Object.entries(
                availablePermissions
              )) {
                permissions[permission] = document.getElementById(
                  id + "-permission-" + permission
                ).checked;
              }
              let obj = {
                isUser: document.getElementById(id + "-isUserPermission")
                  .checked,
                id: document.getElementById(id + "-id").value,
                permissions: permissions,
              };
              const xmlHttp2 = new XMLHttpRequest();
              xmlHttp2.open(
                "POST",
                "../api/v1/guild/permissions/set/" + id,
                true
              );
              xmlHttp2.setRequestHeader("Content-Type", "application/json");
              xmlHttp2.onreadystatechange = function () {
                try {
                  if (xmlHttp2.readyState === 4 && xmlHttp2.status === 200) {
                    const info = JSON.parse(xmlHttp2.responseText);
                    if (info.status === "ok") {
                      let classList = document.getElementById(id + "-warning")
                        .classList;
                      if (!classList.contains("invisible"))
                        classList.add("invisible");
                      fireToast("success", "Saved!");
                      if (id === "create") {
                        document.getElementById("create-card").remove();
                        document.getElementById(
                          "main-container"
                        ).innerHTML += getHtmlPermissionsCard(
                          info.uuid,
                          permissions,
                          availablePermissions,
                          obj.id,
                          obj.isUser
                        );
                      }
                    } else {
                      fireErrorSwal();
                    }
                  }
                } catch (e) {
                  fireErrorSwal();
                  throw e;
                }
              };
              xmlHttp2.send(JSON.stringify(obj));
            } else {
              fireErrorSwal();
            }
          }
        } catch (e) {
          fireErrorSwal();
          throw e;
        }
      };
      xmlHttp1.send();
    } catch (e) {
      fireErrorSwal();
      throw e;
    }
  }

  function remove(id) {
    Swal.fire({
      title: "Are you sure?",
      text: "You won't be able to revert this!",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, delete it!",
    }).then((result) => {
      if (result.value) {
        if (id === "create") {
          document.getElementById(id + "-card").remove();
          fireToast("success", "Deleted!");
        } else {
          const xmlHttp1 = new XMLHttpRequest();
          xmlHttp1.open(
            "GET",
            "../api/v1/guild/permissions/delete/" + id,
            true
          );
          xmlHttp1.onreadystatechange = function () {
            try {
              if (xmlHttp1.readyState === 4 && xmlHttp1.status === 200) {
                const info = JSON.parse(xmlHttp1.responseText);
                if (info.status === "ok") {
                  document.getElementById(id + "-card").remove();
                  fireToast("success", "Deleted!");
                } else {
                  fireErrorSwal();
                }
              }
            } catch (e) {
              fireErrorSwal();
              throw e;
            }
          };
          xmlHttp1.send();
        }
      }
    });
  }

  function clickIsUserPermission(id) {
    if (document.getElementById(id + "-isUserPermission").checked) {
      document.getElementById(id + "-label").innerHTML = "User ID:&nbsp;";
    } else {
      document.getElementById(id + "-label").innerHTML = "Group ID:&nbsp;";
    }
  }

  function renderCards() {
    const xmlHttp1 = new XMLHttpRequest();
    xmlHttp1.open("GET", "../api/v1/guild/permissions/info", true);
    xmlHttp1.onreadystatechange = function () {
      try {
        if (xmlHttp1.readyState === 4 && xmlHttp1.status === 200) {
          const info = JSON.parse(xmlHttp1.responseText);
          if (info.status === "ok") {
            let availablePermissions = info.availablePermissions;
            const xmlHttp2 = new XMLHttpRequest();
            xmlHttp2.open("GET", "../api/v1/guild/permissions/get", true);
            xmlHttp2.onreadystatechange = function () {
              try {
                if (xmlHttp2.readyState === 4 && xmlHttp2.status === 200) {
                  const info = JSON.parse(xmlHttp2.responseText);
                  if (info.status === "ok") {
                    let groups = info.get;
                    let cards = "";
                    for (const [uuid, group] of Object.entries(groups)) {
                      cards += getHtmlPermissionsCard(
                        uuid,
                        group.permissions,
                        availablePermissions,
                        group.id,
                        group.isUser
                      );
                    }
                    document.getElementById(
                      "main-container"
                    ).innerHTML += cards;
                  } else {
                    fireErrorSwal();
                  }
                }
              } catch (e) {
                fireErrorSwal();
                throw e;
              }
            };
            xmlHttp2.send();
          } else {
            fireErrorSwal();
          }
        }
      } catch (e) {
        fireErrorSwal();
        throw e;
      }
    };
    xmlHttp1.send();
  }

  renderCards();
} catch (e) {
  fireErrorSwal();
  throw e;
}
