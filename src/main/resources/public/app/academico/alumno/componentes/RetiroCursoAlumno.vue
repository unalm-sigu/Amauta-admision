<template>
    <div>

        <section class="panel-body m-t-sm">
            <div class="m-b-sm">        
                <h3 class="m-t-n" style="display: inline-block" >Historial de retiro de cursos</h3>
                <div class="pull-right m-t-n">
                </div>
            </div>

            <div v-if="retirosCurso.length">

                <table class="table table-body-hover">
                    <thead>
                        <tr>
                            <th class="v-middle text-left"></th>
                            <th class="col-sm-1 v-middle text-center">Ciclo</th>
                            <th class="col-sm-6 v-middle text-center">Curso</th>
                            <th class="col-sm-4 v-middle text-center">Motivo</th>
                            <th class="col-sm-1 v-middle text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in retirosCurso">
                            <td class="v-middle" >
                                <i v-if="item.esContado" class="fa fa-check-circle fa-2x text-success"></i>
                            </td>
                            <td class="text-center v-middle">
                                <span class="text-left h4 text-primary bold" v-text="item.cicloAcademico.descripcion"></span>
                                <span class="block" v-text="getFechaSmall(item.tramite.fechaRegistro)"></span>
                            </td>
                            <td class="v-middle" >
                                <span class="block m-t-sm">
                                    <span class="text-left h4 text-primary bold" v-text="item.curso.nombre"/>
                                    <span class="block text-muted p-no-margin">
                                        {{item.curso.codigo}} &nbsp;
                                        <i class="fa fa-bookmark text-primary"></i> {{item.curso.tpc}}
                                    </span>
                                    <small class="block text-muted m-b-sm">
                                        Dpto: {{item.curso.departamentoAcademico.nombre}}
                                    </small>
                                </span>
                            </td>
                            <td class="v-middle" >
                                <span v-text="item.tramite.observacion"></span>
                            </td>
                            <td class="v-middle" >
                                <span v-bind:class="classEnumTramite(item.estado)" v-text="item.estadoEnum.value"></span>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div class="card">
                    <div class="card-footer">
                        <div class="row">
                            <div class="col-sm-6 col-md-6">
                                <h3 class="text-primary">Retiros de cursos contables: <span v-text='totalContable'></span></h3>
                            </div>
                            <div class="col-sm-6 col-md-6">
                                <h3 class="text-primary">Total de retiros: <span v-text='total'></span></h3>
                            </div>
                        </div>
                    </div>
                </div>


            </div>
            <div  v-else="">
                <div class="row">
                    <span class="text-muted col-md-12">
                        No tiene historial de retiros de curso
                    </span>
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
                retirosCurso: [],
                total: 0,
                totalContable: 0
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
                    url: APP.url('academico/alumno/retirocurso'),
                    data: {id: $vue.alumno.id},
                    success: function (response) {
                        if (response.success) {

                            $vue.total = response.total;
                            $vue.totalContable = response.data.totalContable;
                            $vue.retirosCurso = response.data.retirosCurso;

                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error() {
                        notify(Messages.errorComunicacion, "error");
                    }
                });
            },
            getFechaSmall(fecha) {
                if (fecha) {
                    return fecha.substring(0, 10);
                }
                return '';
            }
        }
    };
</script>