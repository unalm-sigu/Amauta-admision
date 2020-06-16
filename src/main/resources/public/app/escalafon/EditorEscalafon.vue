<template>
    <div>
        <header class="header padder-lg">
            <h2>Actualizar Escalafón</h2>
        </header>
        <div class="tabbable-xpanel no-padxder">
            <div class="bg-white padder-lg  b-b taxbbable-line nxo-top">
                <ul class="nav nav-tabs">
                    <li><router-link to="/general" class="active">Datos Generales</router-link></li>
                    <li><router-link to="/investigacion">Investigación</router-link></li>
                    <li><router-link to="/idioma">Idiomas</router-link></li>
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

    }
</script>
