/* eslint-disable */
export const ALPHA_NUMERIC_PATTERN = /^[a-zA-ZÀ-ÖÙ-öù-ÿĀ-žḀ-ỿ0-9\s\-\/.]+$/
export const SPACE_CONTAINS_PATTERN = /^\S+$/
export const PHONE_NUMBER_PATTERN = /^(1\s?)?(\d{3}|\(\d{3}\))[\s\-]?\d{3}[\s\-]?\d{4}$/
export const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@_#\$%\^&\*])(?=.{8,})/
export const ONLY_DIGIT_PATTERN = /^\d+$/
export const URL_PATTERN =
	/^(https?:\/\/)?(([a-zA-Z0-9_-]+\.)*[a-zA-Z0-9_-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})?|(localhost|\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}))(:(\d{1,5}))?([\/?][^\s]*)?(#.*)?$/
export const TITLE_PATTERN = /^[^|\\\/-]*$/
export const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i
export const NORMAL_ALPHA_NUM_PATTERN = /^[a-zA-Z0-9]+$/
export const COLOR_CODE_PATTERN = /^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i
export const PHONE_NUMBER_PLUS_ONE_PATTERN = /^\+1\s?(\(\d{3}\)|\d{3})[\s\-]?\d{3}[\s\-]?\d{4}$/
export const FIRST_LAST_NAME_PATTERN = /^[A-Za-z]+(?:[-'][A-Za-z]+)*$/
