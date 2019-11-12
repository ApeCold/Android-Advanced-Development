# FLV 文件格式详解

[TOC]

## 概述

**Flash Video**（简称**FLV**），是一种网络视频格式，用作流媒体格式，它的出现有效地解决了视频文件导入Flash后，使导出的SWF文件体积庞大，不能在网络上有效使用等缺点。 

## FLV文件结构

FLV 文件 = File Header（FLV 文件头）+ File Body（FLV 文件体）。其中文件体又由一系列的 Tag 和 Tag Size 组成。

![FLV_文件结构](图片\FLV_文件结构.jpg)

## FLV File Header

![FLV_File_Header](图片\FLV_File_Header.png)

| 字段        | 字节 | 描述                                                         |
| ----------- | ---- | ------------------------------------------------------------ |
| signature   | 3    | 文件格式标识。（'F‘ : 0x46，'L' : 0x4C，'V' : 0x56）         |
| version     | 1    | 版本号。（0x01：版本号为1）                                  |
| type flags  | 1    | 类型标识。<br />前5个bit是类型标志预留字段，必须是0；<br />第6个bit音频类型标志（TypeFlagsAudio）；<br />第7个bit是类型标志预留字段，必须是0；<br />第8个bit视频类型标志（TypeFlagsVideo）。<br />（0x05，也就是 00000101，表示既有音频也有视频） |
| data offset | 4    | 文件头的长度。（对于版本号为1的FLV文件来说，文件头长度固定是9） |

## FLV File Body 

Body部分是由一系列的 PreviousTagSize + Tag 构成。

> PreviousTagSize 固定为4字节，表示前一个Tag的size
>
> Tag分为3种类型：Script(脚本)、Video(视频)、Audio(音频)

### FLV Tag

FLV中的Tag又分为 tag header（tag头部分）和 tag data（tag数据部分）。

#### FLV Tag Header

| 字段              | 字节 | 描述                                                         |
| ----------------- | ---- | ------------------------------------------------------------ |
| TagType           | 1    | 当前tag的类型，值有：<br />0x08：audio<br />0x09：video<br />0x12：script<br />其他：未使用的预留值 |
| DataSize          | 3    | 数据部分的数据长度（Data字段的数据长度）。                   |
| Timestamp         | 3    | 当前帧的时间戳，单位是毫秒。相对于第一个TAG的时间戳（第一个的时间戳总是0） |
| TimestampExtended | 1    | 时间戳扩展字段。 如果时间戳大于0xFFFFFF，将会使用这个字节。这个字节是时间戳的高8位，上面的三个字节是低24位。 |
| StreamID          | 3    | 流ID，总是0                                                  |
| Data              | n    | 数据部分                                                     |

#### FLV Script Tag

Script Tag一般只有一个，且是FLV中第一个Tag，用来存放一些描述信息，如：duration、width、height等。Script Tag 中Data部分的数据都是以 【数据类型 + 数据长度 + 数据】的格式出现的。一般来说，该Tag Data结构包含两个AMF包。AMF（Action Message Format）是Adobe设计的一种通用数据封装格式，在Adobe的很多产品中应用，简单来说，AMF将不同类型的数据用统一的格式来描述。第一个AMF包封装字符串类型数据，用来装入一个“onMetaData”标志，这个标志与Adobe的一些API调用有，在此不细述。第二个AMF包封装一个数组类型，这个数组中包含了音视频信息项的名称和值。 

![FLV_Script_Tag](图片\FLV_Script_Tag.png)

##### Script Data Type

| 值   | 类型              | 说明                                 |
| ---- | ----------------- | ------------------------------------ |
| 0    | Number type       | double类型，后8字节为数据长度        |
| 1    | Boolean type      | bool类型，后1字节为数据长度          |
| 2    | String type       | string类型，后两字节为数据长度       |
| 3    | Object type       |                                      |
| 4    | MovieClip type    |                                      |
| 5    | Null type         |                                      |
| 6    | Undefined type    |                                      |
| 7    | Reference type    |                                      |
| 8    | ECMA array type   | 数组类型(类似Map)，后4字节为数组长度 |
| 10   | Strict array type |                                      |
| 11   | Date type         |                                      |
| 12   | Long string type  |                                      |

##### 第一个AMF	

![FLV_Script_AMF_1](图片\FLV_Script_AMF_1.png)

> 02：String类型
>
> 0A：String长度为10
>
> 后面10字节为数据的值

#### FLV Video Tag Data

| 字段      | 占位 | 描述                                                         |
| --------- | ---- | ------------------------------------------------------------ |
| FrameType | 4    | 帧类型。<br />1: keyframe(for AVC, a seekable frame)<br />2: inter frame(for AVC, a non-seekable frame)<br />3: disposable inter frame(H.263 only)<br />4: generated keyframe(reserved for server use only)<br />5: video info/command frame |
| CodecID   | 4    | 编码类型。<br />1: JPEG(目前未用到)<br />2: Sorenson H.263<br />3: Screen video<br />4: On2 VP6<br />5: On2 VP6 with alpha channel<br />6: Screen video version 2<br />7: AVC(高级视频编码) |
| VideoData | n    | 数据部分（AVC则需要参考下面AVCVIDEOPACKET部分）              |

##### AVCVIDEOPACKET

| 字段            | 字节 | 描述                                                         |
| --------------- | ---- | ------------------------------------------------------------ |
| AVCPacketType   | 1    | 0：AVC sequence header<br />1：AVC NALU<br />2：AVC end of sequence |
| CompositionTime | 3    | 合成时间。<br />AVCPacketType==1，表示 合成时间(单位毫秒)；<br />否则为0 |
| data            | n    | 如果AVCPacketType==0，数据部分为AVCDecoderConfigurationRecord；<br />如果AVCPacketType==1，数据部分为1个或多个NALU<br />如果AVCPacketType==2，数据部分为空 |

##### AVCDecoderConfigurationRecord

AVCDecoderConfigurationRecord 包含了H.264解码相关比较重要的**sps**和**pps**信息，再给AVC解码器送数据流之前一定要把**sps**和**pps**信息送出，否则的话解码器不能正常解码。而且在解码器stop之后再次start之前，如seek、快进快退状态切换等，都需要重新送一遍sps和pps的信息。AVCDecoderConfigurationRecord在FLV文件中一般情况也是出现1次，也就是第一个video tag。

| 字段        | 字节 | 描述                                          |
| ----------- | ---- | --------------------------------------------- |
| 版本        | 1    | 0x01，版本号为1                               |
| 编码规格    | 3    | sps[1]+sps[2]+sps[3]                          |
| NALU 的长度 | 1    | 0xFF，包长为 （0xFF& 3） + 1，也就是4字节表示 |
| SPS个数     | 1    | 0xE1，个数为0xE1 & 0x1F 也就是1               |
| SPS长度     | 2    | 整个sps的长度                                 |
| sps的内容   | n    | 整个sps                                       |
| pps个数     | 1    | 0x01，1个                                     |
| pps长度     | 2    | 整个pps长度                                   |
| pps内容     | n    | 整个pps内容                                   |

![FLV_Video_Tag_1](图片\FLV_Video_Tag_1.png)



#### FLV Audio Tag Data

与Video Tag类似。

| 字段        | 占位 | 描述                                                         |
| ----------- | ---- | ------------------------------------------------------------ |
| SoundFormat | 4    | 音频数据格式。值：<br />0 = Linear PCM, platform endian <br />1 = ADPCM<br />2 = MP3<br />3 = Linear PCM, little endian<br />4 = Nellymoser 16-kHz mono<br />5 = Nellymoser 8-kHz mono<br />6 = Nellymoser<br />7 = G.711 A-law logarithmic PCM<br />8 = G.711 mu-law logarithmic PCM<br />9 = reserved<br />10 = AAC<br />11 = Speex<br />14 = MP3 8-kHz<br />15 = Device-specific sound<br /> (7，8，14，15是内部预留) |
| SoundRate   | 2    | 音频采样率。值：<br />0 = 5.5-kHz<br />1 = 11-kHz<br />2 = 22-kHz<br />3 = 44-kHz<br />(对于AAC来说，该字段总是3) |
| SoundSize   | 1    | 采样长度。值：<br />0 = snd9Bit<br />1 = snd16Bit<br />(对于压缩过的音频来说，一般都是16bit) |
| SoundType   | 1    | 音频类型(单声道还是双声道)。值：<br />0 = sndMono<br />1 = sndStereo<br />(对于AAC，总是1) |
| SoundData   | n    | 音频数据部分（AAC则需要参考下面AACAUDIODATA部分）            |

##### AACAUDIODATA

| 字段          | 字节 | 描述                                                         |
| ------------- | ---- | ------------------------------------------------------------ |
| AACPacketType | 1    | 0：AAC 序列头<br />1：AAC 数据                               |
| Data          | n    | 如果AACPacketType==0参考下面的 AudioSpecificConfig，如果AACPacketType==1，即AAC原始音频数据 |

##### AudioSpecificConfig

| 字段                   | 占位 |
| ---------------------- | ---- |
| audioObjectType        | 5    |
| samplingFrequencyIndex | 4    |
| channelConfiguration   | 4    |
| frameLengthFlag        | 1    |
| dependsOnCoreCoder     | 1    |
| extensionFlag          | 1    |

