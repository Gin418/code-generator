import FileUploader from '@/components/FileUploader';
import PictureUploader from '@/components/PictureUploader';
import {
  ProCard,
  ProFormInstance,
  ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components';
import {message, UploadFile} from 'antd';
import React, {useEffect, useRef, useState} from 'react';

import {COS_HOST} from '@/constants';
import {
  addGeneratorUsingPost,
  getGeneratorVoByIdUsingGet,
  updateGeneratorUsingPost,
} from '@/services/backend/generatorController';
import {useSearchParams} from '@@/exports';
import {history} from '@umijs/max';

/**
 * 创建生成器页面
 * @constructor
 */

const GeneratorAddPage: React.FC = () => {
  // 获取 URL 中的 id 参数
  const [searchParams] = useSearchParams();
  const id = searchParams.get('id');

  const [oldData, setOldData] = useState<API.GeneratorAddRequest>();

  const formRef = useRef<ProFormInstance>();

  const loadData = async () => {
    if (!id) {
      return;
    }

    try {
      const res = await getGeneratorVoByIdUsingGet({
        // @ts-ignore
        id,
      });
      // 处理文件路径
      if (res.data) {
        const { distPath } = res.data ?? {};
        if (distPath) {
          // @ts-ignore
          res.data.distPath = [
            {
              uid: id,
              name: '文件' + id,
              status: 'done',
              url: COS_HOST + distPath,
              response: distPath,
            } as UploadFile,
          ];
        }
        setOldData(res.data);
        // formRef.current?.setFieldsValue(res.data);
      }
    } catch (e: any) {
      message.error('加载数据失败,' + e.message);
    }
  };

  useEffect(() => {
    if (!id) {
      return;
    }
    loadData();
  }, [id]);

  /**
   * 创建
   * @param values
   */
  const doAdd = async (values: API.GeneratorAddRequest) => {
    try {
      const res = await addGeneratorUsingPost(values);
      if (res.data) {
        message.success('创建成功');
        history.push(`/generator/detail/${res.data}`);
      }
    } catch (e: any) {
      message.error('创建失败,' + e.message);
    }
  };

  /**
   * 更新
   * @param values
   */
  const doUpdate = async (values: API.GeneratorEditRequest) => {
    try {
      const res = await updateGeneratorUsingPost(values);
      if (res.data) {
        message.success('更新成功');
        history.push(`/generator/detail/${id}`);
      }
    } catch (e: any) {
      message.error('更新失败,' + e.message);
    }
  };

  /**
   * 提交
   * @param values
   */
  const doSubmit = async (values: API.GeneratorAddRequest) => {
    console.log('allValues', values);
    // 数据转换
    if (!values.fileConfig) {
      values.fileConfig = {};
    }
    if (!values.modelConfig) {
      values.modelConfig = {};
    }
    // 文件列表转换为 url
    if (values.distPath && values.distPath.length > 0) {
      // @ts-ignore
      values.distPath = values.distPath[0].response;
    }

    // 调用接口
    if (id) {
      await doUpdate({
        // @ts-ignore
        id,
        ...values,
      });
    } else {
      await doAdd(values);
    }
  };

  return (
    <ProCard>
      {/* 创建或者已加载要更新的数据时，才渲染表单，顺利填充默认值 */}
      {(!id || oldData) && (
        <StepsForm<API.GeneratorAddRequest | API.GeneratorEditRequest>
          formRef={formRef}
          formProps={{
            initialValues: oldData,
          }}
          onFinish={doSubmit}
        >
          <StepsForm.StepForm
            name="base"
            title="基本信息"
            onFinish={async () => {
              console.log(formRef.current?.getFieldsValue());
              return true;
            }}
          >
            <ProFormText
              name="name"
              label="生成器名称"
              placeholder="请输入名称"
              rules={[{ required: true }]}
            />
            <ProFormTextArea name="description" label="描述" placeholder="请输入描述" />
            <ProFormText name="basePackage" label="基础包" placeholder="请输入基础包" />
            <ProFormSelect
              name="isGit"
              label="git管理"
              options={[
                {
                  // @ts-ignore
                  value: true,
                  label: 'true',
                },
                {
                  // @ts-ignore
                  value: false,
                  label: 'false',
                },
              ]}
            />
            <ProFormText name="version" label="版本" placeholder="请输入版本" />
            <ProFormText name="author" label="作者" placeholder="请输入作者" />
            <ProFormSelect label="标签" name="tags" mode="tags" placeholder="请输入标签列表" />
            <ProFormItem label="图片" name="picture">
              <PictureUploader biz="generator_picture" />
            </ProFormItem>
          </StepsForm.StepForm>
          <StepsForm.StepForm
            name="fileConfig"
            title="文件配置"
            onFinish={async () => {
              console.log(formRef.current?.getFieldsValue());
              return true;
            }}
          >
            {/* TODO*/}
          </StepsForm.StepForm>
          <StepsForm.StepForm name="modelConfig" title="模型配置">
            {/* TODO*/}
          </StepsForm.StepForm>
          <StepsForm.StepForm name="dist" title="生成器文件">
            <ProFormItem label="产物包" name="distPath">
              <FileUploader biz="generator_dist" description="请上传生成器文件压缩包" />
            </ProFormItem>
          </StepsForm.StepForm>
        </StepsForm>
      )}
    </ProCard>
  );
};

export default GeneratorAddPage;
