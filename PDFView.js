import React,{ requireNativeComponent, Component, PropTypes, View } from 'react-native';

class PDFView extends Component {
    constructor(props) {
        super(props);
        this._onChange = this._onChange.bind(this);
        this._onLayout = this._onLayout.bind(this);
    }

    setNativeProps(nativeProps) {
        this._root.setNativeProps(nativeProps);
    }

    _onChange(event: Event) {
      this.props.onLoadComplete && this.props.onLoadComplete({
        message: event.nativeEvent.message
      });
    }

    _onLayout(event: Event) {
      if (!this.props.onLayout) return
      const thumbnailJpegBase64 = event.nativeEvent.thumbnailJpegBase64
      if (!thumbnailJpegBase64) return

      this.props.onLayout({
        thumbnailJpegBase64
      });
    }

    render() {
        return <PDFCustomView ref={component => this._root = component} {...this.props} onChange={this._onChange} onLayout={this._onLayout} />;
    }
}

PDFView.propTypes = {
    ...View.propTypes,
    src: PropTypes.string.isRequired,
    pageNumber: PropTypes.number,
    zoom: PropTypes.number,
    onLoadComplete: PropTypes.func
};

var PDFCustomView = requireNativeComponent('RCTPDFViewAndroid', PDFView, {
    nativeOnly: {onChange: true, onLayout: true}
});

export default PDFView;
