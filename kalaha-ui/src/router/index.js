import Vue from 'vue';
import VueRouter from 'vue-router';

import Home from '../components/Home.vue'
import Play from '../components/Play.vue'

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Home },
  { path: '/play', component: Play }
];

const router = new VueRouter({routes});

export default router;
