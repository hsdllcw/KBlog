import axios from 'axios'
import Site from '@/api/site';
import { adminApiURI }  from '@/utils'

// initial state
const state = () => (Site.getInstance({}))

// getters
const getters = {
    baseUrl: (site:any) => {
        var port = site.global.port
        return site.global.protocol + '://' + site.domain + (port && (port !== 80 && port !== 443) ? ':' + port : '')
    }
}

// mutations
const mutations = {
    setSite(state, site:any) {
        Object.keys(site).forEach(key=>{
            state[key] = site[key]
        })
    }
}

// actions
const actions = {
    getCurrentSite: ({ commit }) => {
      return new Promise((resolve,rejects)=>{
        axios({
            method: 'get',
            url: `${adminApiURI}/${Site.domain}/first`
        }).then((response:any)=>{
            commit('setSite', response.data.result);
            resolve(response.data.result);

        }).catch(rejects)
      });
    }
}


export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}