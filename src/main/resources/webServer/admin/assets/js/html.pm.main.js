function getHtmlPermissionsCard(uuid, permissions, availablePermissions, id, isUserPermission) {
    let ret = '<div id="' + uuid + '-card" class="row">\n' +
        '                    <div class="col">\n' +
        '                        <div class="card shadow mb-4">\n' +
        '                            <div class="card-header d-flex justify-content-between align-items-center">\n' +
        '                                <p>' + uuid + '&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" data-placement="right" title="This is the UUID of this Permission Group. You can\'t change it and it can be ignored. Its only importent for developers. If there stands &quot;create&quot; its a not saved permission group."></i></p>\n' +
        '                                <div><i id="' + uuid + '-warning" class="fa fa-warning invisible flash animated infinite" data-toggle="tooltip" data-bs-tooltip="" data-placement="left" title="Not all things are saved!" style="color: rgb(255,0,0);margin-left: 10px;"></i><button onclick="save(\'' + uuid + '\');" class="btn btn-primary"\n' +
        '                                        type="button" style="margin-left: 10px;">Save</button><button onclick="remove(\'' + uuid + '\');" class="btn btn-danger" type="button" style="margin-left: 10px;">Delete</button></div>\n' +
        '                            </div>\n' +
        '                            <div class="card-body">\n' +
        '                                <form>\n' +
        '                                    <div class="form-group">\n' +
        '                                        <div class="form-row">\n' +
        '                                            <div class="col"><label class="col-form-label d-inline" for="' + uuid + '-id" id="' + uuid + '-label">' + (isUserPermission ? 'User ID' : 'Group ID') + ':&nbsp;<i class="fa fa-question-circle" data-toggle="tooltip" data-bs-tooltip="" data-placement="right" title="The user or group id can be optained if you enter in discord \'disc0rd/debug id\'. If you enter nothing or 0 this group apply\'s to anybody!"></i></label></div>\n' +
        '                                            <div class="col text-right">\n' +
        '                                                <div class="custom-control custom-switch d-inline"><input ' + (isUserPermission ? 'checked="" ' : '') + 'onclick="clickIsUserPermission(\'' + uuid + '\');clickOnAnything(\'' + uuid + '\');" class="custom-control-input" type="checkbox" id="' + uuid + '-isUserPermission"><label onclick="clickIsUserPermission(\'' + uuid + '\');clickOnAnything(\'' + uuid + '\');" class="custom-control-label" for="' + uuid + '-isUserPermission"><strong>Is User Permission</strong><br></label></div>\n' +
        '                                            </div>\n' +
        '                                        </div>\n' +
        '                                        <div class="form-row">\n' +
        '                                            <div class="col align-self-center"><input onclick="clickOnAnything(\'' + uuid + '\');" id="' + uuid + '-id" class="form-control d-inline" type="text" name="id" style="width: 50%;" value="' + id + '"></div>\n' +
        '                                        </div>\n' +
        '                                    </div>\n'

    for (const [permission, description] of Object.entries(availablePermissions)) {
        ret += '                                    <div class="form-group">\n' +
            '                                        <div class="custom-control custom-switch d-inline"><input id="' + uuid + '-permission-' + permission + '" ' + (permissions[permission] ? 'checked="" ' : '') + 'class="custom-control-input" type="checkbox" onclick="clickOnAnything(\'' + uuid + '\');"><label onclick="clickOnAnything(\'' + uuid + '\');" class="custom-control-label" for="' + uuid + '-permission-' + permission + '"><strong>' + description + '</strong><br></label></div>\n' +
            '                                    </div>\n'
    }

    ret += '                                </form>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>';

    return ret;
}