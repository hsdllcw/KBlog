import axios from 'axios'
// import qs from 'qs'
const { adminApiURI } = require('@/utils')

export abstract class Base {
    domain: String
    constructor(domain: String) {
        this.domain = domain
    }
    deleteById = (ids: any) => {
        let params = {}
        let id: Number
        if (!(ids instanceof Array)) {
            id = parseInt(ids)
            ids = [id]
        }
        return axios({
            method: 'delete',
            url: `${adminApiURI}/${this.domain}/delete`,
            data: ids
        })
    }

    get = (id: Number) => {
        return axios({
            method: 'get',
            url: `${adminApiURI}/${this.domain}/${id}`
        })
    }

    getList = (data: any = {}, page: Number = 0, size: Number = 10) => {
        return axios({
            method: 'post',
            url: `${adminApiURI}/${this.domain}/page/${page}/size/${size}`,
            headers: { 'Content-Type': 'application/json' },
            data: data
        })
    }

    create = (params: any) => {
        return axios({
            method: 'put',
            url: `${adminApiURI}/${this.domain}`,
            headers: { 'Content-Type': 'application/json' },
            data: params
        })
    }

    update = (params: any) => {
        return axios({
            method: 'put',
            url: `${adminApiURI}/${this.domain}/${params.id}`,
            data: params
        })
    }
}
