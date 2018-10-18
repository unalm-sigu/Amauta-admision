Vue.component("vueselect", {
    template: '<select class="form-control"  v-model="valor.id" ><slot /></select>',
    props: {
        valor: {id: null},
    },
    data() {
        return {valor: {id: null}};
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2({minimumResultsForSearch: -1, allowClear: true}).on('change.select2', function (e) {
            vue.valor.id = e.val;
        });
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2({minimumResultsForSearch: -1, allowClear: true}).on('change.select2', function (e) {
            vue.valor.id = e.val;
        });
    },
});