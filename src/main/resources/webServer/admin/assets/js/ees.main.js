function fireErrorSwal() {
    Swal.fire({
        type: 'error',
        title: 'Oops...',
        text: 'Something went wrong!',
        showConfirmButton: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        allowOutsideClick: false
    });
}

function fireSwal(title, description, icon) {
    Swal.fire({
        type: icon,
        title: title,
        text: description
    });
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
        }
    });
    Toast.fire({
        type: icon,
        title: text
    });
}

function fireSuccessToast() {
    fireToast("success", "Success!");
}

try {
    let actionInfos;
    let eventInfos;
    let events;
    const emptyHtml = document.getElementById("main").innerHTML;

    function renderCards() {
        let htmlCards = "";
        let warningSymbol = [];
        let fields = {};

        events.forEach(function (element, identifier) {
            try {
                const tmpObj = document.getElementById("warning-" + element.name);
                if (tmpObj !== null) {
                    if (tmpObj.style.display === "block") warningSymbol.push(element.name);
                }
            } catch (e) {}
            eventInfos.forEach(function (e, i) {
                if (e.name === element.event) {
                    e.config.forEach(function (ele, ide) {
                        try {
                            const path = "event-" + element.name + "-config-" + ele.name;
                            const tmpObj = document.getElementById(path);
                            if (tmpObj !== null) fields[path] = tmpObj.value;
                        } catch (e) {}
                    });
                    Object.keys(element.actions).forEach(function (elem, ident) {
                        getInfo(actionInfos, element.actions[elem].action).config.forEach(function (ele, ide) {
                            try {
                                const path = "event-" + element.name + "-action-" + elem + "-config-" + ele.name;
                                const tmpObj = document.getElementById(path);
                                if (tmpObj !== null) fields[path] = tmpObj.value;
                            } catch (e) {}
                        });
                    });
                    htmlCards = htmlCards + getHtmlEventCard(element.name, element.event, e.description, e.config, element.eventConfig, element.actions, actionInfos);
                }
            });
        });

        document.getElementById("main").innerHTML = emptyHtml + htmlCards;

        warningSymbol.forEach(function (element, identifier) {
            try {
                const tmpObj = document.getElementById("warning-" + element);
                if (tmpObj !== null) {
                    tmpObj.style.display = "block";
                }
            } catch (e) {}
        });

        Object.keys(fields).forEach(function (element, identifier) {
            try {
                const tmpObj = document.getElementById(element);
                if (tmpObj !== null) tmpObj.value = fields[element];
            } catch (e) {}
        })
    }

    function loadCards() {
        const xmlHttp2 = new XMLHttpRequest();
        xmlHttp2.open( "GET", "../api/v1/guild/extendedEvent/get", true );
        xmlHttp2.onreadystatechange = function() {
            try {
                if (xmlHttp2.readyState === 4 && xmlHttp2.status === 200) {
                    const get = JSON.parse(xmlHttp2.responseText);
                    if (get.status === "ok") {
                        events = get.events;

                        renderCards();
                    } else {
                        fireErrorSwal();
                    }
                }
            } catch (e) {
                fireErrorSwal();
                throw e;
            }
        };

        const xmlHttp1 = new XMLHttpRequest();
        xmlHttp1.open( "GET", "../api/v1/guild/extendedEvent/info", true );
        xmlHttp1.onreadystatechange = function() {
            try {
                if (xmlHttp1.readyState === 4 && xmlHttp1.status === 200) {
                    const info = JSON.parse(xmlHttp1.responseText)
                    if (info.status === "ok") {
                        actionInfos = info.info.actionInfos;
                        eventInfos = info.info.eventInfos;
                        xmlHttp2.send( null );
                    } else {
                        fireErrorSwal();
                    }
                }
            } catch (e) {
                fireErrorSwal();
                throw e;
            }
        };
        xmlHttp1.send( null );
    }

    function getInfo(from, name) {
        let tmp;
        from.forEach(function (element, identifier) {
            if (element.name === name) {
                tmp = element;
            }
        });
        return tmp;
    }



    function addEvent() {
        Swal.fire({
            text: 'Please enter a name for the new event:',
            input: 'text',
            showCancelButton: true,
            allowOutsideClick: false,
            inputValidator: (value) => {
                if (!value) {
                    return 'You need to enter a name!';
                } else {
                    let tmp = false;
                    events.forEach(function (element, identifier) {
                        if (element.name === value) tmp = true;
                    });
                    if (tmp) return 'Name already exists!';
                }
            }
        }).then((result) => {
            if (!result.value) return;
            const name = result.value;
            let selectObj = {};
            eventInfos.forEach(function (element, identifier) {
                selectObj[element.name] = element.name;
            });
            Swal.fire({
                text: 'Please select an event:',
                input: 'select',
                inputOptions: selectObj,
                inputValidator: (value) => {
                    if (!value) {
                        return 'You need to enter a name!';
                    }
                },
                showCancelButton: true,
                allowOutsideClick: false
            }).then((result) => {
                if (!result.value) return;
                const xmlHttp3 = new XMLHttpRequest();
                xmlHttp3.open( "POST", "../api/v1/guild/extendedEvent/create", true );
                xmlHttp3.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                xmlHttp3.onreadystatechange = function() {
                    try {
                        if (xmlHttp3.readyState === 4 && xmlHttp3.status === 200) {
                            const info = JSON.parse(xmlHttp3.responseText)
                            if (info.status === "ok") {
                                loadCards();
                                fireSuccessToast();
                            } else {
                                fireErrorSwal();
                            }
                        }
                    } catch (e) {
                        fireErrorSwal();
                        throw e;
                    }
                };
                xmlHttp3.send("name=" + name.replace(/[^a-z0-9]+/gi, '') + "&event=" + result.value);
            });
        });
    }

    function saveEvent(eventName) {
        try {
            document.getElementById("warning-" + eventName).style.display = "none";
            const xmlHttp3 = new XMLHttpRequest();
            xmlHttp3.open( "POST", "../api/v1/guild/extendedEvent/set", true );
            xmlHttp3.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlHttp3.onreadystatechange = function() {
                try {
                    if (xmlHttp3.readyState === 4 && xmlHttp3.status === 200) {
                        const info = JSON.parse(xmlHttp3.responseText);
                        if (info.status === "ok") {
                            fireSuccessToast();
                            loadCards();
                        } else {
                            fireErrorSwal();
                        }
                    }
                } catch (e) {
                    fireErrorSwal();
                    throw e;
                }
            };
            events.forEach(function (element, identifier) {
                if (element.name === eventName) {
                    let actionsJson = {actions:{}};
                    let eventConfigJson = {config:{}};
                    Object.keys(element.actions).forEach(function (e, i) {
                        const actionInfo = getInfo(actionInfos, element.actions[e].action);
                        actionsJson["actions"][e] = {};
                        actionsJson["actions"][e]["config"] = {};
                        actionsJson["actions"][e]["action"] = actionInfo.name;
                        actionInfo.config.forEach(function (ele, ide) {
                            actionsJson["actions"][e]["config"][ele.name] = document.getElementById("event-" + eventName + "-action-" + e + "-config-" + ele.name).value
                        });
                    });
                    let eventInfo;
                    events.forEach(function (ele, ide) {
                        if (ele.name === eventName) eventInfo = getInfo(eventInfos, ele.event);
                    });
                    eventInfo.config.forEach(function (ele, ide) {
                        eventConfigJson["config"][ele.name] = document.getElementById("event-" + eventName + "-config-" + ele.name).value;
                    });
                    xmlHttp3.send("name=" + eventName + "&actionsJson=" + encodeURIComponent(JSON.stringify(actionsJson)) + "&eventConfigJson=" + encodeURIComponent(JSON.stringify(eventConfigJson)));
                }
            });
        } catch (e) {
            fireErrorSwal();
            throw e;
        }
    }

    function addAction(eventName) {
        let selectObj = {};
        actionInfos.forEach(function (element, identifier) {
            selectObj[element.name] = element.name;
        });
        Swal.fire({
            text: 'Please select an action:',
            input: 'select',
            inputOptions: selectObj,
            inputValidator: (value) => {
                if (!value) {
                    return 'You need to select a action!';
                }
            },
            showCancelButton: true,
            allowOutsideClick: false
        }).then((result) => {
            if (!result.value) return;
            document.getElementById('warning-' + eventName).style.display = 'block';
            events.forEach(function (element, identifier, obj) {
                if (element.name === eventName) {
                    let nextNumber = 1;
                    Object.keys(element.actions).forEach(function (e, i) {
                        try {
                            if (parseInt(e) >= nextNumber) nextNumber = parseInt(e) + 1;
                        } catch (e) {}
                    });
                    obj[identifier].actions[nextNumber] = {};
                    obj[identifier].actions[nextNumber].action = result.value;
                    obj[identifier].actions[nextNumber].config = {};
                    renderCards();
                }
            });
        });
    }

    function deleteEvent(eventName) {
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.value) {
                const xmlHttp3 = new XMLHttpRequest();
                xmlHttp3.open( "POST", "../api/v1/guild/extendedEvent/delete", true );
                xmlHttp3.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                xmlHttp3.onreadystatechange = function() {
                    try {
                        if (xmlHttp3.readyState === 4 && xmlHttp3.status === 200) {
                            const info = JSON.parse(xmlHttp3.responseText);
                            if (info.status === "ok") {
                                fireSuccessToast();
                                loadCards();
                            } else {
                                fireErrorSwal();
                            }
                        }
                    } catch (e) {
                        fireErrorSwal();
                        throw e;
                    }
                };
                xmlHttp3.send("name=" + eventName);
            }
        });
    }

    function deleteAction(eventName, actionId) {
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.value) {
                document.getElementById("warning-" + eventName).style.display = "block";
                events.forEach(function (element, identifier, object) {
                    if (element.name === eventName) {
                        delete object[identifier].actions[actionId];
                        renderCards();
                    }
                });
            }
        });
    }

    function reOrderAction(eventName, actionId) {
        Swal.fire({
            title: 'Please enter a new ID:',
            text: 'The bot will work from lowest to highest id.',
            html: '<input id="swal-input1" type="number" class="swal2-input" value="' + actionId + '">',
            focusConfirm: false,
            showCancelButton: true,
            allowOutsideClick: false,
            preConfirm: () => {
                return document.getElementById('swal-input1').value;
            }
        }).then((response) => {
            if (response.value) {
                let ret = false;
                events.forEach(function (element, identifier) {
                    if (element.name === eventName) {
                        Object.keys(element.actions).forEach(function (e, i) {
                            if (response.value === e) ret = true;
                        });
                        if (!ret) {
                            document.getElementById("warning-" + eventName).style.display = "block";
                            element.actions[response.value] = element.actions[actionId];
                            delete element.actions[actionId];
                            renderCards();
                        }
                    }
                });
                if (ret) {
                    fireSwal('Sorry,', 'This id is already taken.', 'error');
                }
            } else if (response.dismiss) {
            } else {
                fireSwal('Sorry,', 'You need to enter a id!', 'error');
            }
        });
    }

    loadCards();

} catch (e) {
    fireErrorSwal();
    throw e;
}
