<template>
    <div>
        <header class="header padder-lg">
            <h2>Actualizar Escalafón</h2>
            <div class="pull-right m-t-md">                 
                <a class="btn btn-info btn-sm" v-bind:href="origen">Regresar</a>
            </div>  
        </header>
        <div class="tabbable-xpanel no-padxder">
            <div class="bg-white padder-lg  b-b taxbbable-line nxo-top">
                <ul class="nav nav-tabs">
<!--                    <li><router-link to="/general" class="active">Datos Generales</router-link></li>-->
                  <li :class="{ 'active': isActive('/general') }">
                    <router-link to="/general">Datos Generales</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/investigacion') }">
                    <router-link to="/investigacion">Investigación</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/academico') }">
                    <router-link to="/academico">Académico</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/idioma') }">
                    <router-link to="/idioma">Idiomas</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/distincion') }">
                    <router-link to="/distincion">Distinción</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/experiencia') }">
                    <router-link to="/experiencia">Experiencia</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/produccion') }">
                    <router-link to="/produccion">Producción</router-link>
                  </li>
                  <li :class="{ 'active': isActive('/experiencia/asesor') }">
                    <router-link to="/experiencia/asesor">Experiencia Asesor</router-link>
                  </li>
                </ul>
            </div>
        </div>
        <section class="wrapper-lg">
            <section class="panel">
                <router-view></router-view>
            </section>
        </section>
    </div>
</template>
<script>
    module.exports = {
        components: {},
        data() {
            return{
                escalafon: JSON.parse(escalafonJson),
                origen: origen
            };
        },
        computed: {
        },
        mounted() {
            this.$store.commit('SET_ESCALAFON', this.escalafon);
        },
        methods: {
          isActive(path) {
            return this.$route.path === path;
          }
        }
    };
</script>

<style scoped>
.nav-tabs .active a {
  background-color: #fff;
  color: #000;
}
</style>