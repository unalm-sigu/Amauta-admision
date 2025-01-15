<template>
    <modal-vik ref="modalChangeAula"
               v-bind="modalChangeAula"
               v-bind:okaction="saveChangeAula">
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
                        <div class="col-md-4">
                            <label>Vacantes</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.vacantes}}
                            </span>
                        </div>
                        <div class="col-md-4">
                            <label>Matriculados</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.matriculados}}
                            </span>
                        </div>
                        <div class="col-md-4">
                            <label>Disponibles</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.disponibles}}
                            </span>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Aula</label>
                                <multiselect v-model="cursoNiv.aula"
                                             v-bind:options="aulas"
                                             v-bind:allow-empty="false"
                                             v-on:search-change="searchAulaDebounce"
                                             v-on:input="selectAula"
                                             track-by="id"
                                             placeholder="Seleccione un aula"
                                             v-bind:internal-search="false"
                                             v-bind:showNoOptions="true"
                                             v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="text-primary h4">{{ props.option.codigo }}</span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="block">
                                            <span class="bold"> {{ props.option.codigo }} </span>
                                            <span v-if="props.option.nombre" class=""> - {{ props.option.nombre }} </span>
                                        </span>
                                        <span class="block">
                                            Cap: {{ props.option.capacidadAula }} - 
                                            Edif: {{ props.option.aulaSuperior.nombre}}
                                        </span>
                                    </template>

                                    <template slot="noOptions">Lista vacía</template>
                                    <template slot="noResult">Sin resultados</template>

                                </multiselect>
                                <input v-bind:value="getObjectId(cursoNiv.aula)" type="text" class="hide"/>
                            </div>
                        </div>

                        <template v-if="cursoNiv.aula">
                            <div class="col-md-4">
                                <label>Edificio / Pabellón</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{ cursoNiv.aula.aulaSuperior.nombre }}
                                </span>
                            </div>

                            <div class="col-md-4">
                                <label>Capacidad / Aforo</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{ cursoNiv.aula.capacidadAula }} /
                                    {{ cursoNiv.aula.aforo }}
                                </span>
                            </div>
                        </template>
                    </div>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Indique el motivo del cambio</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" rows="3"></textarea>
                            </div>
                        </div>
                    </div>

                    <div v-if="hayCruceAula" class="alert alert-danger">
                        <h4>{{mensajeCruceAula}}</h4>
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
                aulas: [],
                hayCruceAula: false,
                mensajeCruceAula: "",
                ciclo: JSON.parse(cicloJson),
                form: "id-form-change-aula",
                title: "Cambio de aula",
                modalChangeAula: VUE_MODAL.structFormAjax({
                    id: "id-modal-change-aula",
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
            this.searchAulaDebounce = debounce(this.searchAula, 800);
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.hayCruceAula = false;
                this.mensajeCruceAula = null;

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalChangeAula.open();
                myUtils.activarNumeric();
            },

            searchAula(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchAula?nombre=${nombre}`
                    })).then((resp) => this.aulas = resp.data.data);
                }
            },
            selectAula(item) {
                this.hayCruceAula = false;
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    aula: {id: item.id},
                    grupoHoras: {id: this.cursoNiv.grupoHoras.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/verificarCruceAula`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.hayCruceAula = data.hayCruceAula;
                    this.mensajeCruceAula = data.mensajeCruceAula;
                });
            },

            saveChangeAula() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                const payload = {
                    id: this.cursoNiv.id,
                    aula: {id: this.cursoNiv.aula.id},
                    motivoCambio: this.cursoNiv.motivoCambio
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/changeAula`,
                    modal: this.$refs.modalChangeAula,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalChangeAula;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>