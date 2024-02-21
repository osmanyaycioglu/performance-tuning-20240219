// package com.adenon.library.common;
//
// import org.osgi.framework.BundleActivator;
// import org.osgi.framework.BundleContext;
//
// import com.adenon.library.common.sequence.ISequenceGeneratorFactory;
// import com.adenon.library.common.sequence.SequenceGeneratorFactory;
//
// public class CommonActivator implements BundleActivator {
//
// private static BundleContext context;
// private ISequenceGeneratorFactory sequenceGeneratorFactory;
//
// static BundleContext getContext() {
// return context;
// }
//
// @Override
// public void start(BundleContext bundleContext) throws Exception {
// CommonActivator.context = bundleContext;
// this.sequenceGeneratorFactory = SequenceGeneratorFactory.getSequenceGeneratorFactory();
// bundleContext.registerService(ISequenceGeneratorFactory.class.getName(), this.sequenceGeneratorFactory, null);
// }
//
// @Override
// public void stop(BundleContext bundleContext) throws Exception {
// CommonActivator.context = null;
// }
//
// }
