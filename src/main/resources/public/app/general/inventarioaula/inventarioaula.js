new Vue({
    el: '#main',
    data: {
        inventario: {},
        inventarios: [],
        aula: {id: idaula},
        inventarioURL:APP.url('general/aula/inventario/'+idaula+'/all')
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        console.log(idaula);
    },
    methods: {
        allInventario() {
            let $vue = this;
        }
    }
});







        