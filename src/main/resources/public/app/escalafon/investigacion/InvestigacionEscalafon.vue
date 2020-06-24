<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Investigación</button>
        <h3 class="page-header"> Investigación </h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="text-left">Título</th>
                    <th class="text-left">Investigadores</th>
                    <th class="text-left">Área</th>
                    <th class="text-left">Repositorio</th>
                    <th class="text-left">Fecha</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.investigacionEscalafon" class="editor">
                <tr>
                    <td class="text-left">
                        {{ item.titulo  }}
                    </td>
                    <td class="text-left">
                        {{item.investigadores}}
                    </td>
                    <td class="text-left">
                        {{item.area.descripcion}}
                    </td>
                    <td class="text-left">
                        <a class="pointer" target="_blank" v-bind:href='item.urlRepositorio'>{{item.urlRepositorio}} </a>
                    </td>
                    <td class="text-left">
                        <small class="block text-muted"> Fecha Inicio: {{item.fechaInicio}}</small>
                        <small class="block text-muted">Fecha Final: {{item.fechaFin}}</small>
                    </td>
                    <td class="text-left">
                        <div class="dropdown actions">
                            <a class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                            <ul class="dropdown-menu pull-right">
                                <li><a class="pointer" v-on:click="openModal(item)">Editar</a></li>
                                <li v-if="!item.confirmado" ><a class="pointer" v-on:click="openModalConfirmacion(item)">Confirmar</a></li>
                                <li><a class="pointer" v-on:click="eliminar(item, index)">Eliminar</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
        <investigacion-escalafon-form ref="investigacionEscalafonFormVUE"></investigacion-escalafon-form>
        <confirmar-escalafon-form ref="confirmarEscalafonFormVUE"></confirmar-escalafon-form>
    </div>
</template>

<script>
    const InvestigacionEscalafonForm = httpVueLoader(package + '/investigacion/InvestigacionEscalafonForm.vue');
    const ConfirmarEscalafonForm = httpVueLoader(package + '/ConfirmarEscalafonForm.vue');

    module.exports = {
        components: {InvestigacionEscalafonForm, ConfirmarEscalafonForm},
        data() {
            return{
                rutaModulo: "/escalafon/investigacion"
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
                this.$refs.investigacionEscalafonFormVUE.open(itemClone);
            },
            openModalConfirmacion(item) {
                let tipo = "InvestigacionEsc";
                this.$refs.confirmarEscalafonFormVUE.open(item.id, tipo);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListInvestigacionEscalafon", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListInvestigacionEscalafon(response);
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
            setListInvestigacionEscalafon(item) {
                this.$store.commit('SET_LIST_INVESTIGACION', item.data.data);
            }
        }
    };
</script>
