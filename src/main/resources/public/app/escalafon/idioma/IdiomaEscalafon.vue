<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Idioma</button>
        <h3 class="page-header"> Idiomas </h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="text-left">Idioma</th>
                    <th class="text-center">Materno</th>
                    <th class="text-center">Nivel Conversación</th>
                    <th class="text-center">Nivel Lectura</th>
                    <th class="text-center">Nivel Escritura</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.idiomaEscalafon" class="editor">
                <tr>
                    <td class="text-left">
                        {{item.idioma.nombre == "Otros" ? item.idiomaOtro : item.idioma.nombre}}
                    </td>
                    <td class="text-center">
                        <i v-if="item.lenguaMaterna" class="fa fa-check fa-lg"></i>
                    </td>
                    <td class="text-center">
                        {{item.conversacion}}
                    </td>
                    <td class="text-center">
                        {{item.lectura}}
                    </td>
                    <td class="text-center">
                        {{item.escritura}}
                    </td>
                    <td class="text-center">
                        <div class="dropdown actions">
                            <a class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                            <ul class="dropdown-menu pull-right">
                                <li><a class="pointer" v-on:click="openModal(item)">Editar</a></li>
                                <li><a class="pointer" v-on:click="eliminar(item, index)">Eliminar</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
        <idioma-form ref="idiomaFormVUE"></idioma-form>
    </div>
</template>
<div th:replace="_modules/vue-modal-confirm"></div>

<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    const IdiomaForm = httpVueLoader(package + '/idioma/IdiomaEscalafonForm.vue');

    module.exports = {
        components: {IdiomaForm},
        data() {
            return{
                rutaModulo: "/escalafon/idioma/"
            };
        },
        computed: {
            escalafon() {
                return this.$store.state.escalafon;
            }
        },
        mounted() {
        },
        methods: {
            openModal(item) {
                let itemClone = Object.assign({}, item)
                this.$refs.idiomaFormVUE.open(itemClone);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListIdiomaEscalafon", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListIdiomaEscalafon(response);
                            }
                        });
            },
            eliminar(item, index) {
                let $vue = this;
                bootbox.confirm({
                    message: Messages.confirmDelete,
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            axios.post($vue.rutaModulo + "/eliminar", item)
                                    .then(function (response) {
                                        if (response.data.success) {
                                            notify(response.data.message, "success");
                                            $vue.loadList();
                                        } else {
                                            notify(response.data.message, 'warning');
                                        }
                                    })
                                    .catch(function (error) {
                                        notify(error.errorComunicacion, "error");
                                    });
                        }
                    }
                });
            },
            setListIdiomaEscalafon(item) {
                this.$store.commit('SET_LIST_IDIOMA', item.data.data);
            }
        }
    };
</script>
