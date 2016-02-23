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
      this.props.onLayout && this.props.onLayout({
        message: event.nativeEvent.message,
        bytes: event.nativeEvent.bytes
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
