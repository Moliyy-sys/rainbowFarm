import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.2,
      warn: true,
    })
  ],
  shortcuts: {
    'flex-center': 'flex justify-center items-center',
    'flex-between': 'flex justify-between items-center',
    'flex-col-center': 'flex flex-col justify-center items-center',
    'card-shadow': 'shadow-md hover:shadow-lg transition-shadow duration-300',
    'btn-primary': 'bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded transition-colors',
    'btn-success': 'bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded transition-colors',
    'btn-warning': 'bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded transition-colors',
    'btn-danger': 'bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded transition-colors',
    'text-primary': 'text-blue-600',
    'text-success': 'text-green-600',
    'text-warning': 'text-yellow-600',
    'text-danger': 'text-red-600',
    'border-primary': 'border border-blue-200',
    'page-container': 'min-h-screen bg-gray-50',
    'content-card': 'bg-white rounded-lg shadow-sm p-6',
    'form-item': 'mb-4',
    'table-header': 'bg-gray-50 font-medium text-gray-700',
    // 面包屑导航相关样式
    'breadcrumb-item': 'flex items-center px-3 py-1.5 rounded-md transition-all duration-200 hover:bg-gray-50 cursor-pointer',
    'breadcrumb-current': 'bg-blue-50 text-blue-600 cursor-default font-semibold',
    'breadcrumb-separator': 'flex items-center mx-1 text-gray-400 text-xs',
    'breadcrumb-hover': 'hover:shadow-sm hover:scale-105 hover:text-blue-600'
  },
  theme: {
    colors: {
      primary: {
        50: '#eff6ff',
        100: '#dbeafe',
        500: '#3b82f6',
        600: '#2563eb',
        700: '#1d4ed8'
      },
      success: {
        50: '#f0fdf4',
        100: '#dcfce7',
        500: '#22c55e',
        600: '#16a34a'
      },
      warning: {
        50: '#fffbeb',
        100: '#fef3c7',
        500: '#f59e0b',
        600: '#d97706'
      },
      danger: {
        50: '#fef2f2',
        100: '#fee2e2',
        500: '#ef4444',
        600: '#dc2626'
      }
    }
  }
})
