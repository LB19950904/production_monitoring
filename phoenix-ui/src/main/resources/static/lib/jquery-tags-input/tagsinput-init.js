//tag input
$(function () {
    // 收件人邮箱
    $('textarea[name="alarmMailboxEmails"]').tagsInput({
        width: 'auto',
        'delimiter': ';',
        onChange: function (elem, elem_tags) {
            $('.tag').css({'background-color': '#D2D2D2', 'border-color': '#D2D2D2'});
        }
    });
    // 短信收信人号码
    $('textarea[name="alarmSmsPhoneNumbers"]').tagsInput({
        width: 'auto',
        'delimiter': ';',
        onChange: function (elem, elem_tags) {
            $('.tag').css({'background-color': '#D2D2D2', 'border-color': '#D2D2D2'});
        }
    });
    // 钉钉收信人号码
    $('textarea[name="alarmDingtalkPhoneNumbers"]').tagsInput({
        width: 'auto',
        'delimiter': ';',
        onChange: function (elem, elem_tags) {
            $('.tag').css({'background-color': '#D2D2D2', 'border-color': '#D2D2D2'});
        }
    });
    // 企业微信收信人号码
    $('textarea[name="alarmEnterpriseWechatPhoneNumbers"]').tagsInput({
        width: 'auto',
        'delimiter': ';',
        onChange: function (elem, elem_tags) {
            $('.tag').css({'background-color': '#D2D2D2', 'border-color': '#D2D2D2'});
        }
    });
    // 飞书收信人号码
    $('textarea[name="alarmFeishuUserIds"]').tagsInput({
        width: 'auto',
        'delimiter': ';',
        onChange: function (elem, elem_tags) {
            $('.tag').css({'background-color': '#D2D2D2', 'border-color': '#D2D2D2'});
        }
    });
});
