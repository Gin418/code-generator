import FileUploader from '@/components/FileUploader';
import { makeGeneratorUsingPost } from '@/services/backend/generatorController';
import { ProFormInstance, ProFormItem } from '@ant-design/pro-components';
import { ProForm } from '@ant-design/pro-form';
import { Collapse, message } from 'antd';
import { saveAs } from 'file-saver';
import { useRef, useState } from 'react';

interface Props {
  meta: any;
}

export default (props: Props) => {
  const { meta } = props;
  const formRef = useRef<ProFormInstance>();
  const [downloading, setDownloading] = useState<boolean>(false);

  /**
   * 提交
   * @param values
   */
  const doSubmit = async (values: API.GeneratorMakeRequest) => {
    setDownloading(true);
    console.log('allValues', values);

    // 文件列表转换为 url
    const zipFilePath = values.zipFilePath;
    if (!zipFilePath || zipFilePath.length < 1) {
      message.error('请上传模板文件压缩包');
      return;
    }

    // @ts-ignore
    values.zipFilePath = values.zipFilePath[0].response;

    try {
      // 调用接口
      const blob = await makeGeneratorUsingPost(
        {
          meta,
          zipFilePath: values.zipFilePath,
        },
        {
          responseType: 'blob',
        },
      );
      // 使用 file-saver 来保存文件
      saveAs(blob, meta.name + '.zip');
    } catch (e: any) {
      message.error('制作失败');
    }

    setDownloading(false);
  };

  const formView = (
    <ProForm
      loading={downloading}
      formRef={formRef}
      submitter={{
        searchConfig: {
          submitText: '制作',
        },
        resetButtonProps: {
          hidden: true,
        },
      }}
      onFinish={doSubmit}
    >
      <ProFormItem label="模板文件" name="zipFilePath">
        <FileUploader
          biz="generator_make_template"
          description="请上传模板文件压缩包，打包时不用添加最外层目录！！"
        />
      </ProFormItem>
    </ProForm>
  );

  return (
    <Collapse
      items={[
        {
          key: 'maker',
          label: '生成器制作工具',
          children: <>{formView}</>,
        },
      ]}
    />
  );
};
