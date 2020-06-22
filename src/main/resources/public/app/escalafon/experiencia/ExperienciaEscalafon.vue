<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Experiencia</button>
        <h3 class="page-header"> Experiencia </h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Institución</th>
                    <th>Cargo</th>
                    <th>Fecha</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.experienciaEscalafon" class="editor">
                <tr>
                    <td>
                        {{item.universidad.id != null ? item.universidad.nombre : item.institucion }}
                    </td>  
                    <td>
                        {{item.cargo}}
                    </td>
                    <td>
                        <small class="block text-muted"> Fecha Inicio: {{item.fechaInicio}}</small>
                        <small class="block text-muted">Fecha Final: {{item.fechaFin}}</small>
                    </td>
                    <td class="text-center">
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
        <experiencia-form ref="experienciaFormVUE"></experiencia-form>
        <confirmar-escalafon-form ref="confirmarEscalafonFormVUE"></confirmar-escalafon-form>
    </div>
</template>

<script>
    const ExperienciaForm = httpVueLoader(package + '/experiencia/ExperienciaEscalafonForm.vue');
    const ConfirmarEscalafonForm = httpVueLoader(package + '/ConfirmarEscalafonForm.vue');

    module.exports = {
        components: {ExperienciaForm, ConfirmarEscalafonForm},
        data() {
            return{
                rutaModulo: "/escalafon/experiencia/"
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
                this.$refs.experienciaFormVUE.open(itemClone);
            },
            openModalConfirmacion(item) {
                let tipo = "ExperienciaEsc";
                this.$refs.confirmarEscalafonFormVUE.open(item.id, tipo);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListExperienciaEscalafon", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListExperienciaEscalafon(response);
                            }
                        });
            },
            eliminar(item, index) {
                let $vue = this;
                bootbox.confirm({
                    message: MESSAGES.confirmDelete,
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            axios.post($vue.rutaModulo + "/eliminar", {id: item.id})
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
            setListExperienciaEscalafon(item) {
                this.$store.commit('SET_LIST_EXP', item.data.data);
            }
        }
    };
</script>
