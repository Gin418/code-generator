import { COS_HOST } from '@/constants';
import { uploadFileUsingPost } from '@/services/backend/fileController';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { message, Upload, UploadProps } from 'antd';
import React, { useState } from 'react';
import ImgCrop from "antd-img-crop";

interface Props {
  biz: string;
  onChange?: (url: string) => void;
  value?: string;
}

/**
 * 图片上传组件
 * @constructor
 */
const PictureUploader: React.FC<Props> = (props: Props) => {
  const { biz, onChange, value } = props;
  const [loading, setLoading] = useState(false);

  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    disabled: loading,
    listType: 'picture-card',
    showUploadList: false,
    customRequest: async (fileObj: any) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost(
          {
            biz,
          },
          {},
          fileObj.file,
        );
        // 拼接完整图片地址
        const fullPath = COS_HOST + res.data;
        onChange?.(fullPath ?? '');
        fileObj.onSuccess(res.data);
      } catch (e: any) {
        message.error('上传失败，' + e.message);
        fileObj.onError(e);
      }
      setLoading(false);
    },
  };

  /**
   * 上传按钮
   */
  const uploadButton = (
    <button style={{ border: 0, background: 'none' }} type="button">
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>上传</div>
    </button>
  );

  return (
    <ImgCrop rotationSlider aspect={4 / 3}>
    <Upload {...uploadProps}>
      {value ? <img src={value} alt="picture" style={{ width: '100%' }} /> : uploadButton}
    </Upload>
    </ImgCrop>
  );
};
export default PictureUploader;
