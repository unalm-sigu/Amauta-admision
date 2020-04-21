Vue.component("button-download", {
    template:
            '<button v-bind:disabled="isactivedownloadbtn" v-on:click="btnactiondownload">' +
            '<span v-if="isactivedownloadbtn"><i class="fa fa-spinner fa-spin fa-3x fa-fw"></i> Generando reporte...</span>' +
            '<slot v-else=""></slot></button>',
    props: {
        isactivedownloadbtn: {type: Boolean, default: false},
        btnactiondownload: {type: Function, default: () => {
            }},
        urldownload: {type: String, default: ''},
        postdata: {type: Object, default: {}},
    },
    methods: {
        btnactiondownload: function (vue) {
            let $vue = this;
            $vue.isactivedownloadbtn = true;
            axios({
                url: APP.url($vue.urldownload),
                method: 'POST',
                responseType: 'blob', // important
                data: $vue.postdata,
            }).then((response) => {
                var namee = response
                        .headers["content-disposition"]
                        .replace("attachment; filename=", "")
                        .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();
                $vue.isactivedownloadbtn = false;
            }).catch(error => {
                $vue.isactivedownloadbtn = false;
                notify(GlobalMessages.errorComunicacion, "error");
            });
        }
    }
});
