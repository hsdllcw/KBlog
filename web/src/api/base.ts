import axios from 'axios'
import { Loading, Message } from 'element-ui'
import { ElLoadingComponent } from 'element-ui/types/loading'
// import qs from 'qs'
const { adminApiURI } = require('@/utils')

let loading: ElLoadingComponent;

function starLoading() {
    loading = Loading.service({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
    })
}

axios.interceptors.request.use(
    config => {
        starLoading()
        return Promise.resolve(config)
    }
)

axios.interceptors.response.use(
    response => {
        loading.close()
        return Promise.resolve(response)
    },
    error => {
        loading.close()
        if (error[`response`][`data`]) {
            if (error[`response`][`data`][`message`]) {
                Message.error(error[`response`][`data`][`message`])
            } else {
                (<Array<any>>error[`response`][`data`][`result`]).forEach(element => {
                    setTimeout(() => {
                        Message.error(element[`defaultMessage`])
                    }, 500)
                })
            }
        } else {
            Message.error("请求失败...")
        }
        return Promise.reject(error)
    }
)

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
