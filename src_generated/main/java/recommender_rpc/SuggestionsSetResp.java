// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: recom_rpc.proto

package recommender_rpc;

/**
 * Protobuf type {@code recommender_rpc.SuggestionsSetResp}
 */
public  final class SuggestionsSetResp extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:recommender_rpc.SuggestionsSetResp)
    SuggestionsSetRespOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SuggestionsSetResp.newBuilder() to construct.
  private SuggestionsSetResp(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SuggestionsSetResp() {
    sessionId_ = 0L;
    suggestions_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SuggestionsSetResp(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 8: {

            sessionId_ = input.readInt64();
            break;
          }
          case 18: {
            if (!((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
              suggestions_ = new java.util.ArrayList<recommender_rpc.SuggesionData>();
              mutable_bitField0_ |= 0x00000002;
            }
            suggestions_.add(
                input.readMessage(recommender_rpc.SuggesionData.parser(), extensionRegistry));
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
        suggestions_ = java.util.Collections.unmodifiableList(suggestions_);
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return recommender_rpc.RecommenderRpcProto.internal_static_recommender_rpc_SuggestionsSetResp_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return recommender_rpc.RecommenderRpcProto.internal_static_recommender_rpc_SuggestionsSetResp_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            recommender_rpc.SuggestionsSetResp.class, recommender_rpc.SuggestionsSetResp.Builder.class);
  }

  private int bitField0_;
  public static final int SESSIONID_FIELD_NUMBER = 1;
  private long sessionId_;
  /**
   * <code>int64 sessionId = 1;</code>
   */
  public long getSessionId() {
    return sessionId_;
  }

  public static final int SUGGESTIONS_FIELD_NUMBER = 2;
  private java.util.List<recommender_rpc.SuggesionData> suggestions_;
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  public java.util.List<recommender_rpc.SuggesionData> getSuggestionsList() {
    return suggestions_;
  }
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  public java.util.List<? extends recommender_rpc.SuggesionDataOrBuilder> 
      getSuggestionsOrBuilderList() {
    return suggestions_;
  }
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  public int getSuggestionsCount() {
    return suggestions_.size();
  }
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  public recommender_rpc.SuggesionData getSuggestions(int index) {
    return suggestions_.get(index);
  }
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  public recommender_rpc.SuggesionDataOrBuilder getSuggestionsOrBuilder(
      int index) {
    return suggestions_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (sessionId_ != 0L) {
      output.writeInt64(1, sessionId_);
    }
    for (int i = 0; i < suggestions_.size(); i++) {
      output.writeMessage(2, suggestions_.get(i));
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (sessionId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, sessionId_);
    }
    for (int i = 0; i < suggestions_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, suggestions_.get(i));
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof recommender_rpc.SuggestionsSetResp)) {
      return super.equals(obj);
    }
    recommender_rpc.SuggestionsSetResp other = (recommender_rpc.SuggestionsSetResp) obj;

    boolean result = true;
    result = result && (getSessionId()
        == other.getSessionId());
    result = result && getSuggestionsList()
        .equals(other.getSuggestionsList());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SESSIONID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSessionId());
    if (getSuggestionsCount() > 0) {
      hash = (37 * hash) + SUGGESTIONS_FIELD_NUMBER;
      hash = (53 * hash) + getSuggestionsList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static recommender_rpc.SuggestionsSetResp parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static recommender_rpc.SuggestionsSetResp parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static recommender_rpc.SuggestionsSetResp parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static recommender_rpc.SuggestionsSetResp parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(recommender_rpc.SuggestionsSetResp prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code recommender_rpc.SuggestionsSetResp}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:recommender_rpc.SuggestionsSetResp)
      recommender_rpc.SuggestionsSetRespOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return recommender_rpc.RecommenderRpcProto.internal_static_recommender_rpc_SuggestionsSetResp_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return recommender_rpc.RecommenderRpcProto.internal_static_recommender_rpc_SuggestionsSetResp_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              recommender_rpc.SuggestionsSetResp.class, recommender_rpc.SuggestionsSetResp.Builder.class);
    }

    // Construct using recommender_rpc.SuggestionsSetResp.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getSuggestionsFieldBuilder();
      }
    }
    public Builder clear() {
      super.clear();
      sessionId_ = 0L;

      if (suggestionsBuilder_ == null) {
        suggestions_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
      } else {
        suggestionsBuilder_.clear();
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return recommender_rpc.RecommenderRpcProto.internal_static_recommender_rpc_SuggestionsSetResp_descriptor;
    }

    public recommender_rpc.SuggestionsSetResp getDefaultInstanceForType() {
      return recommender_rpc.SuggestionsSetResp.getDefaultInstance();
    }

    public recommender_rpc.SuggestionsSetResp build() {
      recommender_rpc.SuggestionsSetResp result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public recommender_rpc.SuggestionsSetResp buildPartial() {
      recommender_rpc.SuggestionsSetResp result = new recommender_rpc.SuggestionsSetResp(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.sessionId_ = sessionId_;
      if (suggestionsBuilder_ == null) {
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
          suggestions_ = java.util.Collections.unmodifiableList(suggestions_);
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.suggestions_ = suggestions_;
      } else {
        result.suggestions_ = suggestionsBuilder_.build();
      }
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof recommender_rpc.SuggestionsSetResp) {
        return mergeFrom((recommender_rpc.SuggestionsSetResp)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(recommender_rpc.SuggestionsSetResp other) {
      if (other == recommender_rpc.SuggestionsSetResp.getDefaultInstance()) return this;
      if (other.getSessionId() != 0L) {
        setSessionId(other.getSessionId());
      }
      if (suggestionsBuilder_ == null) {
        if (!other.suggestions_.isEmpty()) {
          if (suggestions_.isEmpty()) {
            suggestions_ = other.suggestions_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureSuggestionsIsMutable();
            suggestions_.addAll(other.suggestions_);
          }
          onChanged();
        }
      } else {
        if (!other.suggestions_.isEmpty()) {
          if (suggestionsBuilder_.isEmpty()) {
            suggestionsBuilder_.dispose();
            suggestionsBuilder_ = null;
            suggestions_ = other.suggestions_;
            bitField0_ = (bitField0_ & ~0x00000002);
            suggestionsBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getSuggestionsFieldBuilder() : null;
          } else {
            suggestionsBuilder_.addAllMessages(other.suggestions_);
          }
        }
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      recommender_rpc.SuggestionsSetResp parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (recommender_rpc.SuggestionsSetResp) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private long sessionId_ ;
    /**
     * <code>int64 sessionId = 1;</code>
     */
    public long getSessionId() {
      return sessionId_;
    }
    /**
     * <code>int64 sessionId = 1;</code>
     */
    public Builder setSessionId(long value) {
      
      sessionId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 sessionId = 1;</code>
     */
    public Builder clearSessionId() {
      
      sessionId_ = 0L;
      onChanged();
      return this;
    }

    private java.util.List<recommender_rpc.SuggesionData> suggestions_ =
      java.util.Collections.emptyList();
    private void ensureSuggestionsIsMutable() {
      if (!((bitField0_ & 0x00000002) == 0x00000002)) {
        suggestions_ = new java.util.ArrayList<recommender_rpc.SuggesionData>(suggestions_);
        bitField0_ |= 0x00000002;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        recommender_rpc.SuggesionData, recommender_rpc.SuggesionData.Builder, recommender_rpc.SuggesionDataOrBuilder> suggestionsBuilder_;

    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public java.util.List<recommender_rpc.SuggesionData> getSuggestionsList() {
      if (suggestionsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(suggestions_);
      } else {
        return suggestionsBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public int getSuggestionsCount() {
      if (suggestionsBuilder_ == null) {
        return suggestions_.size();
      } else {
        return suggestionsBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public recommender_rpc.SuggesionData getSuggestions(int index) {
      if (suggestionsBuilder_ == null) {
        return suggestions_.get(index);
      } else {
        return suggestionsBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder setSuggestions(
        int index, recommender_rpc.SuggesionData value) {
      if (suggestionsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSuggestionsIsMutable();
        suggestions_.set(index, value);
        onChanged();
      } else {
        suggestionsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder setSuggestions(
        int index, recommender_rpc.SuggesionData.Builder builderForValue) {
      if (suggestionsBuilder_ == null) {
        ensureSuggestionsIsMutable();
        suggestions_.set(index, builderForValue.build());
        onChanged();
      } else {
        suggestionsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder addSuggestions(recommender_rpc.SuggesionData value) {
      if (suggestionsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSuggestionsIsMutable();
        suggestions_.add(value);
        onChanged();
      } else {
        suggestionsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder addSuggestions(
        int index, recommender_rpc.SuggesionData value) {
      if (suggestionsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSuggestionsIsMutable();
        suggestions_.add(index, value);
        onChanged();
      } else {
        suggestionsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder addSuggestions(
        recommender_rpc.SuggesionData.Builder builderForValue) {
      if (suggestionsBuilder_ == null) {
        ensureSuggestionsIsMutable();
        suggestions_.add(builderForValue.build());
        onChanged();
      } else {
        suggestionsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder addSuggestions(
        int index, recommender_rpc.SuggesionData.Builder builderForValue) {
      if (suggestionsBuilder_ == null) {
        ensureSuggestionsIsMutable();
        suggestions_.add(index, builderForValue.build());
        onChanged();
      } else {
        suggestionsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder addAllSuggestions(
        java.lang.Iterable<? extends recommender_rpc.SuggesionData> values) {
      if (suggestionsBuilder_ == null) {
        ensureSuggestionsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, suggestions_);
        onChanged();
      } else {
        suggestionsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder clearSuggestions() {
      if (suggestionsBuilder_ == null) {
        suggestions_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
      } else {
        suggestionsBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public Builder removeSuggestions(int index) {
      if (suggestionsBuilder_ == null) {
        ensureSuggestionsIsMutable();
        suggestions_.remove(index);
        onChanged();
      } else {
        suggestionsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public recommender_rpc.SuggesionData.Builder getSuggestionsBuilder(
        int index) {
      return getSuggestionsFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public recommender_rpc.SuggesionDataOrBuilder getSuggestionsOrBuilder(
        int index) {
      if (suggestionsBuilder_ == null) {
        return suggestions_.get(index);  } else {
        return suggestionsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public java.util.List<? extends recommender_rpc.SuggesionDataOrBuilder> 
         getSuggestionsOrBuilderList() {
      if (suggestionsBuilder_ != null) {
        return suggestionsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(suggestions_);
      }
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public recommender_rpc.SuggesionData.Builder addSuggestionsBuilder() {
      return getSuggestionsFieldBuilder().addBuilder(
          recommender_rpc.SuggesionData.getDefaultInstance());
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public recommender_rpc.SuggesionData.Builder addSuggestionsBuilder(
        int index) {
      return getSuggestionsFieldBuilder().addBuilder(
          index, recommender_rpc.SuggesionData.getDefaultInstance());
    }
    /**
     * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
     */
    public java.util.List<recommender_rpc.SuggesionData.Builder> 
         getSuggestionsBuilderList() {
      return getSuggestionsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        recommender_rpc.SuggesionData, recommender_rpc.SuggesionData.Builder, recommender_rpc.SuggesionDataOrBuilder> 
        getSuggestionsFieldBuilder() {
      if (suggestionsBuilder_ == null) {
        suggestionsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            recommender_rpc.SuggesionData, recommender_rpc.SuggesionData.Builder, recommender_rpc.SuggesionDataOrBuilder>(
                suggestions_,
                ((bitField0_ & 0x00000002) == 0x00000002),
                getParentForChildren(),
                isClean());
        suggestions_ = null;
      }
      return suggestionsBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:recommender_rpc.SuggestionsSetResp)
  }

  // @@protoc_insertion_point(class_scope:recommender_rpc.SuggestionsSetResp)
  private static final recommender_rpc.SuggestionsSetResp DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new recommender_rpc.SuggestionsSetResp();
  }

  public static recommender_rpc.SuggestionsSetResp getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SuggestionsSetResp>
      PARSER = new com.google.protobuf.AbstractParser<SuggestionsSetResp>() {
    public SuggestionsSetResp parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SuggestionsSetResp(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SuggestionsSetResp> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SuggestionsSetResp> getParserForType() {
    return PARSER;
  }

  public recommender_rpc.SuggestionsSetResp getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

