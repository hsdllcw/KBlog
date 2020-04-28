import axios from 'axios'
import qs from 'qs'
import $ from 'jquery'
import { isDev, adminApiURI } from '@/utils'

export function login(userInfo) {
  return new Promise(resolve => {
    axios.get(`${isDev ? '/proxy' : String()}/login`).then(loginData => {
      userInfo[`_csrf`] = $(loginData['data']).find(`input[name='_csrf']`).val()
      axios.post(`${isDev ? '/proxy' : String()}/login`, qs.stringify(userInfo)).then(() => {
        resolve({
          data: {
            token: userInfo[`_csrf`]
          }
        })
      }).catch(() => {
        resolve({
          data: {
            token: userInfo[`_csrf`]
          }
        })
      })
    })
  })
}

export function userInfo() {
  return axios.get(`${adminApiURI}/user/current`)
}

export function logout() {
  return new Promise(resolve => {
    axios.post(`${isDev ? '/proxy' : String()}/logout`).then(resolve).catch(resolve)
  })
}
