Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#permisoProgramacionVUE',
    data: {
        list: APP.url("permisoprograma/buscar/list")
//                [{colaborador: {id: 1,
//                    anexo: [
//                        {id: 1, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]},
//                        {id: 2, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]},
//                        {id: 3, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]}
//                    ]
//                }},
//            {colaborador: {id: 2,
//                    anexo: [
//                        {id: 1, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]},
//                        {id: 2, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]},
//                        {id: 3, permisos1: [{id: 1}, {id: 2}, {id: 3}], permisos2: [{id: 1}, {id: 2}, {id: 3}]}
//                    ]
//                }}
//        ]
    },
    computed: {

    },
    mounted: function () {
        let $vue = this;

    },
    methods: {

    }
});
