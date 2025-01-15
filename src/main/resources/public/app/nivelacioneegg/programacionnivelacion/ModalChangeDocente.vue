<template>
    <modal-vik ref="modalChangeDocente"
               v-bind="modalChangeDocente"
               v-bind:okaction="saveChangeDocente">
        <div slot="body">

            <h3 class="text-primary block m-b m-t">{{title}} {{ciclo.descripcion}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row">
                        <div class="col-md-9">
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.cursoCiclo.curso.codigo}} -
                                {{cursoNiv.cursoCiclo.curso.nombre}}
                                <br>
                                Sección: {{cursoNiv.codigo}}
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                {{cursoNiv.horasDictado}} horas
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                Del {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                            </span>
                        </div>

                        <div class="col-md-3">
                            <span class="item-form-control item-form-gray text-primary">
                                Grupo horario
                                <br>
                                {{cursoNiv.grupoHoras.codigo}}
                            </span>
                        </div>
                    </div>

                    <div class="row m-t-md">
                        <div class="col-md-10">
                            <div class="form-group">
                                <label>Docente</label>
                                <multiselect v-model="cursoNiv.docente"
                                             v-bind:options="docentes"
                                             v-bind:allow-empty="false"
                                             v-on:search-change="searchDocenteDebounce"
                                             v-on:input="selectDocente"
                                             track-by="id"
                                             placeholder="Seleccione un docente"
                                             v-bind:internal-search="false"
                                             v-bind:showNoOptions="true"
                                             v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="text-primary h4">
                                            <template v-if="props.option.persona">
                                                {{ props.option.persona.apellidosNombres }}
                                            </template>
                                            <template v-else="">
                                                Desconocido
                                            </template>
                                        </span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="block">
                                            <span class="bold text-primary"> 
                                                {{ props.option.codigo }} - 
                                                <template v-if="props.option.persona">
                                                    {{ props.option.persona.apellidosNombres }}
                                                </template>
                                                <template v-else="">
                                                    Desconocido
                                                </template>
                                            </span>
                                        </span>
                                        <span v-if="props.option.departamentoAcademico" class="block">
                                            Dpto: {{ props.option.departamentoAcademico.nombre }} - 
                                            Fac: {{ props.option.departamentoAcademico.facultad.nombre }}
                                        </span>
                                    </template>

                                    <template slot="noOptions">Lista vacía</template>
                                    <template slot="noResult">Sin resultados</template>

                                </multiselect>
                                <input v-bind:value="getObjectId(cursoNiv.docente)" required="true" type="text" class="hide"/>
                            </div>
                        </div>

                        <div class="col-md-2">
                            <template v-if="cursoNiv.docente">
                                <label>Código</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{cursoNiv.docente.codigo}}
                                </span>
                            </template>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Indique el motivo del cambio</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" rows="3"></textarea>
                            </div>
                        </div>
                    </div>

                    <div v-if="hayCruceDocente" class="alert alert-danger">
                        <h4>{{mensajeCruceDocente}}</h4>
                    </div>


                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                visible: false,
                curso: null,
                cursoNiv: null,
                raptor: null,
                docentes: [],
                hayCruceDocente: false,
                mensajeCruceDocente: "",
                ciclo: JSON.parse(cicloJson),
                form: "id-form-change-docente",
                title: "Cambio del docente",
                modalChangeDocente: VUE_MODAL.structFormAjax({
                    id: "id-modal-change-docente",
                    okbtn: "Guardar cambio",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                })
            };
        },

        mounted() {},
        computed: {
        },

        created() {
            this.searchDocenteDebounce = debounce(this.searchDocente, 800);
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.hayCruceDocente = false;
                this.mensajeCruceDocente = null;

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalChangeDocente.open();
                myUtils.activarNumeric();
            },

            searchDocente(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchDocente?nombre=${nombre}`
                    })).then((resp) => this.docentes = resp.data.data);
                }
            },
            selectDocente(item) {
                this.hayCruceDocente = false;
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    docente: {id: item.id, codigo: item.codigo},
                    grupoHoras: {id: this.cursoNiv.grupoHoras.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/verificarCruceDocente`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.hayCruceDocente = data.hayCruceDocente;
                    this.mensajeCruceDocente = data.mensajeCruceDocente;
                });
            },

            saveChangeDocente() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                const payload = {
                    id: this.cursoNiv.id,
                    docente: {
                        id: this.cursoNiv.docente.id,
                        codigo: this.cursoNiv.docente.codigo
                    },
                    motivoCambio: this.cursoNiv.motivoCambio
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/changeDocente`,
                    modal: this.$refs.modalChangeDocente,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalChangeDocente;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>