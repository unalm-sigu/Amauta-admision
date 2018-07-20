var app = new Vue({
    el: '#reunionConsejo',
    data: {
        btnActive: 'lista',
        onlyOne: true
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, watch: {
        btnActive: function (after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.fullcalendar.render();
                vue.onlyOne = false;
            }
        }
    }, methods: {
        btnActive: function (after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.fullcalendar.render();
                vue.onlyOne = false;
            }
        },
        eventClick: function (self, date, jsEvent, view) {
        },
        dayClick: function (self, date, jsEvent, view) {
        },
        dayDbClick: function (self, date, element) {
            var vue = this;

            //var dia = date.format("DD/MM/YYYY HH:mm:ss");
            var dia = date.format("DD/MM/YYYY");
            alert(dia);

        }
    }
})