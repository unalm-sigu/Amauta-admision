<template>
    <div>

        <section class="panel-body m-t-sm">
            <div class="m-b-sm">        
                <h3 class="m-t-n" style="display: inline-block" >Historial de retiro de ciclos</h3>
                <div class="pull-right m-t-n">
                </div>
            </div>
            <div v-if="totalRetiros == 0 ">
                <div class="row">
                    <span class="text-muted col-md-12">
                        No tiene historial de retiros de ciclo
                    </span>
                </div>
            </div>
            <div v-else="">

                <table class="table table-body-hover">
                    <thead>
                        <tr>
                            <th class="col-sm-1 v-middle text-left"></th>
                            <th class="col-sm-2 v-middle text-center">Ciclo</th>
                            <th class="col-sm-5 v-middle text-center">Motivo</th>
                            <th class="col-sm-2 v-middle text-center">Tipo</th>
                            <th class="col-sm-1 v-middle text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in retirosCiclo">
                            <td>
                                <i v-if="item.estado == 'ACEP' &AMP;&AMP; item.cicloAcademico.tipo=='REG'" class="fa fa-check-circle fa-2x text-success"></i>
                            </td>
                            <td>
                                <span v-text="item.cicloAcademico.descripcion2"></span>
                            </td>
                            <td>
                                <span v-text='item.motivo'></span>
                            </td>
                            <td>
                                <span v-text="item.tipoEnum.value"></span>
                            </td>
                            <td>
                                <span v-bind:class="classEnumTramite(item.estado)" v-text="item.estadoEnum.value"></span>
                            </td>
                            <!--                        <td class="text-center">
                                                        <span v-if="item.aplicado"><i class="fa fa-check-circle fa-lg text-primary"></i></span>
                                                        <span v-else="">
                                                            <a class="pointer" v-on:click="aplicarRetiro(item)">
                                                                <i class="fa fa-times-circle fa-2x text-danger"></i>
                                                            </a>
                                                        </span>
                                                    </td>-->
                        </tr>
                    </tbody>
                </table>

                <div class="card">
                    <div class="card-footer">
                        <div class="row">
                            <div class="col-sm-6 col-md-6">
                                <h3 class="text-primary">Retiros de ciclos contables: <span v-text='totalCicloContable'></span></h3>
                            </div>
                            <div class="col-sm-6 col-md-6">
                                <h3 class="text-primary">Total de retiros: <span v-text='totalRetiros'></span></h3>
                            </div>
                        </div>
                    </div>
                </div>


            </div>

        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>

    </div>

</template>
<script>

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {
        props: {
            alumno: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                retirosCiclo: [],
                totalRetiros: 0,
                totalCicloContable: 0
            };
        },

        mounted() {

        },

        methods: {
            classEnumTramite(item) {
                switch (item) {
                    case 'COMP':
                    case 'APR':
                    case 'ACEP':
                        return "label label-success";
                        break;
                    case 'ENV':
                    case 'ENV':
                    case 'DEV':
                    case 'CRE':
                    case 'ACT':
                        return "label label-info";
                        break;
                    case 'CANC':
                    case 'RCHZ':
                    case 'NPAG':
                    case 'RCHCS':
                    case 'ANU':
                        return "label label-danger";
                        break;
                    default:
                        return "label label-default";
                }
            },
            obtenerDatos() {
                let $vue = this;
                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/retirociclo'),
                    data: {id: $vue.alumno.id},
                    success: function (response) {
                        if (response.success) {

                            $vue.totalRetiros = response.data.totalRetiros;
                            $vue.totalCicloContable = response.data.totalCicloContable;
                            $vue.retirosCiclo = response.data.retiroCiclo;

                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error() {
                        notify(Messages.errorComunicacion, "error");
                    }
                });
            },
            aplicarRetiro(item) {
                let $vue = this;
                bootbox.confirm({
                    message: '¿Está seguro que desea aplicar este Retiro de Ciclo en el historial del alumno?',
                    buttons: {
                        confirm: {label: 'Sí, aplicar', className: "btn-warning"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {

                            MODAL.showWait("Espere un momento por favor");
                            $.ajax({
                                method: 'GET',
                                url: APP.url('academico/alumno/aplicarRetiroCiclo'),
                                data: {id: item.id},
                                success: function (response) {
                                    if (response.success) {
                                        $vue.totalRetiros = response.data.totalRetiros;
                                        $vue.totalCicloContable = response.data.totalCicloContable;
                                        $vue.retirosCiclo = response.data.retiroCiclo;

                                        $global.$emit('reset-loading-data-alumno');

                                    } else {
                                        notify(response.message, "error");
                                    }
                                    MODAL.hideWait();
                                },
                                error() {
                                    MODAL.hideWait();
                                    notify(Messages.errorComunicacion, "error");
                                }
                            });

                        }
                    }
                });
            }
        }
    };
</script>