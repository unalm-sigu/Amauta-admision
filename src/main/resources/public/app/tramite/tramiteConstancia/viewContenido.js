Vue.component("multiselect", window.VueMultiselect.default)
new Vue({
    el: '#main',
    data: {
        contenido: contenidoJson,
        id: id,

    },
    computed: {

    },
    created() {
    },
    mounted: function () {
        var myFrame = $("#myframe").contents().find('body');
        myFrame.html(this.contenido);

    },
    methods: {
        downloadWord() {
            var $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/downloadWord/' + this.id),
                success: function (response) {

                }, error: function () {

                }
            });
        }
    }
});
