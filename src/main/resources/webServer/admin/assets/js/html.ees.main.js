function getOrDefault(value, key, defaultValue) {
    try {
        if (value) {
            if (value[key]) {
                return value[key];
            }
        }
    } catch (e) {}
    return defaultValue;
}

function getHtmlEventCard(eventName, event, eventDescription, eventConfig, eventConfigValues, actions, actionInfos) {
    let ret = '          <div style="margin-top: 10px;" id="eventCard-' + eventName + '" class="card shadow">\n' +
        '                    <div class="card-header py-3">\n' +
        '                        <div class="row">\n' +
        '                            <div class="col">\n' +
        '                                <p class="text-primary d-xl-flex align-items-xl-center m-0 font-weight-bold" style="height: 38px;font-size: 23px;">' + eventName + '</p>\n' +
        '                            </div>\n' +
        '                            <div class="col text-right d-xl-flex justify-content-xl-end align-items-xl-center"><i id="warning-' + eventName + '" class="fa fa-warning" data-toggle="tooltip" data-bs-tooltip="" title="Not Saved Data!" style="display:none; color: rgb(255,0,0);"></i><button class="btn btn-primary" type="button" style="margin-left: 10px;" onclick="saveEvent(\'' + eventName + '\');">Save</button><button class="btn btn-primary" type="button" style="margin-left: 10px;" onclick="addAction(\'' + eventName + '\');">Add Action</button><button class="btn btn-danger"\n' +
        '                                    type="button" style="margin-left: 10px;" onclick="deleteEvent(\'' + eventName + '\');">Delete Event</button></div>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                    <div class="card-body">\n' +
        '                        <div class="card">\n' +
        '                            <div class="card-body">\n' +
        '                                <div class="row">\n' +
        '                                    <div class="col d-xl-flex justify-content-xl-start align-items-xl-center">\n' +
        '                                        <h4><strong>' + event + '</strong>&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" title="' + eventDescription + '"></i></h4>\n' +
        '                                    </div>\n' +
        '                                </div>\n' +
        '                                <form onsubmit="return false;" style="margin-top: 10px;">\n';
    eventConfig.forEach(function (element, identifier) {
        ret = ret + '                          <label for="eventConfig' + element.name + '">' + element.name + ':&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" title="' + element.description + '"></i></label><input onkeypress="document.getElementById(\'warning-' + eventName + '\').style.display = \'block\';" id="event-' + eventName + '-config-' + element.name + '" class="form-control" type="text" name="eventConfig' + element.name + '" autocomplete="off" value="' + getOrDefault(eventConfigValues, element.name, "") + '">';
    });
    ret = ret + '                         </form>\n' +
        '                            </div>\n' +
        '                        </div>\n';
    if (actions) {
        Object.keys(actions).forEach(function (element, identifier) {
            const action = actions[element];
            const id = element;
            actionInfos.forEach(function (e, i) {
                if (e.name === action.action) {
                    ret = ret + '                        <div class="card" style="margin-top: 10px;">\n' +
                        '                            <div class="card-body">\n' +
                        '                                <div class="row">\n' +
                        '                                    <div class="col d-xl-flex justify-content-xl-start align-items-xl-center">\n' +
                        '                                        <h4><strong>' + id + ':</strong> ' + action.action + '&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" title="' + e.description + '"></i></h4>\n' +
                        '                                    </div>\n' +
                        '                                    <div class="col text-right"><button onclick="reOrderAction(\'' + eventName + '\', \'' + id + '\');" class="btn btn-primary" type="button" style="margin-left: 10px;">Re- Order Action</button><button onclick="deleteAction(\'' + eventName + '\', \'' + id + '\');" class="btn btn-danger" type="button" style="margin-left: 10px;">Delete Action</button></div>\n' +
                        '                                </div>\n' +
                        '                                <form onsubmit="return false;" margin-top: 10px;">';
                    e.config.forEach(function (ele, ide) {
                        ret = ret + '                                    <label for="actionConfigField">' + ele.name + ':&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" title="' + ele.description + '"></i></label><input id="event-' + eventName + '-action-' + id + '-config-' + ele.name + '" onkeypress="document.getElementById(\'warning-' + eventName + '\').style.display = \'block\';" class="form-control" type="text" name="actionConfigField" autocomplete="off" value="' + getOrDefault(action.config, ele.name, "") + '">';
                    });
                    ret = ret + '                                </form>\n' +
                        '                            </div>\n' +
                        '                        </div>\n';
                }
            });
        });
    }
    ret = ret +    '         </div>\n' +
        '                </div>';

    return ret;
}
